package com.brackinscarroll.cybersecurityqrnfc.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.brackinscarroll.cybersecurityqrnfc.R;
import com.brackinscarroll.cybersecurityqrnfc.common.Common;
import com.brackinscarroll.cybersecurityqrnfc.interfaces.MainFragmentListener;
import com.brackinscarroll.cybersecurityqrnfc.views.KeyAuthenticateFragment;
import com.brackinscarroll.cybersecurityqrnfc.views.KeyGenerateFragment;
import com.brackinscarroll.cybersecurityqrnfc.views.MainFragment;
import com.brackinscarroll.cybersecurityqrnfc.views.MainNavigationDrawerFragment;
import com.brackinscarroll.cybersecurityqrnfc.views.SimpleReadFragment;
import com.brackinscarroll.cybersecurityqrnfc.views.SimpleWriteFragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class MainActivity extends ActionBarActivity implements MainFragmentListener
{
    private KeyPair m_keyPair = null;
    private Key m_publicKey = null;
    private Key m_privateKey = null;
    private boolean m_keyPairGenerated = false;

    private final String m_publicFileName = "public.key";
    private final String m_privateFileName = "private.key";
    private final int m_keyLength = 1024;

    public static final String KEY_PAIR_GENERATED = "KeyPairFile";

    //private data
    private NfcAdapter _nfcAdapter;
    Tag _tag;
    private String _result;
    private int _currentFragment = -1;

    //fragments
    private MainFragment _mainFragment;
    private KeyGenerateFragment _keyGenerateFragment;
    private KeyAuthenticateFragment _keyAuthenticateFragment;
    private SimpleReadFragment _simpleReadFragment;
    private SimpleWriteFragment _simpleWriteFragment;
    private MainNavigationDrawerFragment _navigationDrawerFragment;

    public MainActivity()
    {
    }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        _navigationDrawerFragment = ( MainNavigationDrawerFragment ) getFragmentManager().findFragmentById( R.id.main_navigation_drawer );


        if( savedInstanceState == null )
        {

        }
        else
        {
            _currentFragment = savedInstanceState.getInt( Common.Activities.MainActivity.CURRENT_FRAGMENT );
            switch( _currentFragment )
            {
                case 0:
                    _mainFragment = ( MainFragment ) getFragmentManager().findFragmentById( R.id.main_activity_frame );
                    break;
                case 1:
                    _keyGenerateFragment = ( KeyGenerateFragment ) getFragmentManager().findFragmentById( R.id.main_activity_frame );
                    break;
                case 2:
                {
                    _keyAuthenticateFragment = ( KeyAuthenticateFragment ) getFragmentManager().findFragmentById( R.id.main_activity_frame );
                    break;
                }
                case 3:
                {
                    _simpleReadFragment = ( SimpleReadFragment ) getFragmentManager().findFragmentById( R.id.main_activity_frame );
                    break;
                }
                case 4:
                {
                    _simpleWriteFragment = ( SimpleWriteFragment ) getFragmentManager().findFragmentById( R.id.main_activity_frame );
                    break;
                }
            }
        }

        _navigationDrawerFragment.setUp( R.id.main_navigation_drawer, ( DrawerLayout ) findViewById( R.id.main_drawer_layout ) );
        _nfcAdapter = NfcAdapter.getDefaultAdapter( this );

        if( _nfcAdapter == null )
        {
            //Indicate device doesn't have nfc and force close.
            Toast.makeText( this, getString( R.string.str_msg_formatexception), Toast.LENGTH_LONG ).show();
            finish();
            return;

        }
        if( !_nfcAdapter.isEnabled() )
        {
            Toast.makeText( this, getString( R.string.str_msg_nfc_disabled), Toast.LENGTH_LONG ).show();
        }
        intentHandler( getIntent() );
        SharedPreferences settings = getSharedPreferences(KEY_PAIR_GENERATED, MODE_PRIVATE);
        m_keyPairGenerated = settings.getBoolean("keyPairGenerated", false);

        if (m_keyPairGenerated)
        {
            readKeys(getApplicationContext());
        }
    }

    @Override
    protected void onResume ( )
    {
        super.onResume();

        SharedPreferences settings = getSharedPreferences(KEY_PAIR_GENERATED, MODE_PRIVATE);
        m_keyPairGenerated = settings.getBoolean("keyPairGenerated", false);

        if (m_keyPairGenerated && m_privateKey == null && m_publicKey == null)
        {
            readKeys(getApplicationContext());
        }


        //Activity should be resumed, otherwise you enter illegal state
        startForegroundDispatch( this, _nfcAdapter );
    }


    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if( id == R.id.action_settings )
        {
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onSaveInstanceState( Bundle bundle )
    {
        super.onSaveInstanceState( bundle );
        //Put stuff into the bundle
        bundle.putInt( Common.Activities.MainActivity.CURRENT_FRAGMENT, _currentFragment );
    }


    @Override
    public void onPause()
    {
        //Should stop dispatch before you pause.
        stopForegroundDispatch( this, _nfcAdapter );
        super.onPause();
    }


    @Override
    public void onNavigationDrawerItemSelected( int position )
    {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        _currentFragment = position;
        Log.d( Common.Activities.MainActivity.TAG,
                Common.Activities.MainActivity.ON_NAV_DRAWER + getString( R.string.str_msg_nav_position) + position );
        switch( position )
        {
            case Common.Fragments.MainFragment.ID:
            {
                _mainFragment = new MainFragment();
                fragmentManager.beginTransaction()
                        .replace( R.id.main_activity_frame, _mainFragment, Common.Fragments.MainFragment.TAG )
                        .commit();
                break;
            }
            case Common.Fragments.KeyGenerateFragment.ID:
            {

                _keyGenerateFragment = new KeyGenerateFragment();
                fragmentManager.beginTransaction()
                        .replace( R.id.main_activity_frame, _keyGenerateFragment )
                        .commit();
                break;
            }
            case Common.Fragments.KeyAuthenticateFragment.ID:
            {
                _keyAuthenticateFragment = new KeyAuthenticateFragment();
                fragmentManager.beginTransaction()
                        .replace( R.id.main_activity_frame, _keyAuthenticateFragment )
                        .commit();
                break;
            }
            case Common.Fragments.SimpleReadFragment.ID:
            {
                _simpleReadFragment = new SimpleReadFragment();
                fragmentManager.beginTransaction()
                        .replace( R.id.main_activity_frame, _simpleReadFragment, Common.Fragments.SimpleReadFragment.TAG )
                        .commit();
                break;
            }
            case Common.Fragments.SimpleWriteFragment.ID:
            {
                _simpleWriteFragment = new SimpleWriteFragment();
                fragmentManager.beginTransaction()
                        .replace( R.id.main_activity_frame, _simpleWriteFragment, Common.Fragments.SimpleWriteFragment.TAG )
                        .commit();
                break;
            }
        }
    }

    @Override
    public void aboutApp()
    {
        new AlertDialog.Builder( this )
                .setTitle( R.string.app_name )
                .setMessage( getString( R.string.str_about_desc ) )
                .setPositiveButton( R.string.str_cool, new DialogInterface.OnClickListener()
                {
                    public void onClick( DialogInterface dialog, int which )
                    {
                        //NOTHING
                    }
                } )
                .setIcon( R.drawable.ic_action_person )
                .show();
    }


    @Override
    protected void onNewIntent( Intent intent )
    {
        intentHandler( intent );
    }


    public static void startForegroundDispatch( final Activity activity, NfcAdapter adapter )
    {
        final Intent intent = new Intent( activity.getApplicationContext(), activity.getClass() );
        intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );

        final PendingIntent pendingIntent = PendingIntent.getActivity( activity.getApplicationContext(), 0, intent, 0 );

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction( NfcAdapter.ACTION_NDEF_DISCOVERED );
        filters[0].addCategory( Intent.CATEGORY_DEFAULT );
        try
        {
            filters[0].addDataType( Common.MIME_TEXT_PLAIN );
        }
        catch( IntentFilter.MalformedMimeTypeException e )
        {
            throw new RuntimeException( activity.getString( R.string.str_msg_malformedmime) );
        }

        adapter.enableForegroundDispatch( activity, pendingIntent, filters, techList );
    }

    public static void stopForegroundDispatch( final Activity activity, NfcAdapter adapter )
    {
        adapter.disableForegroundDispatch( activity );
    }


    private void intentHandler( Intent intent )
    {
        String action = intent.getAction();
        if( NfcAdapter.ACTION_NDEF_DISCOVERED.equals( action ) )
        {

            String type = intent.getType();
            if( Common.MIME_TEXT_PLAIN.equals( type ) )
            {

                _tag = intent.getParcelableExtra( NfcAdapter.EXTRA_TAG );
                new NdefReaderTask().execute( _tag );

            }
            else
            {
                Log.d( Common.Activities.MainActivity.TAG, getString( R.string.str_msg_wrong_mime ) + type );
            }
        }
        else if( NfcAdapter.ACTION_TECH_DISCOVERED.equals( action ) )
        {
            //Sometimes this would get activated...
            Tag tag = intent.getParcelableExtra( NfcAdapter.EXTRA_TAG );
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for( String tech : techList )
            {
                if( searchedTech.equals( tech ) )
                {
                    new NdefReaderTask().execute( tag );
                    break;
                }
            }
        }
    }


    private NdefRecord createRecord( String text ) throws UnsupportedEncodingException
    {

        //create the message
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes( getString( R.string.str_ascii) );
        int langLength = langBytes.length;
        int textLength = textBytes.length;

        byte[] payload = new byte[1 + langLength + textLength];
        payload[0] = ( byte ) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy( langBytes, 0, payload, 1, langLength );
        System.arraycopy( textBytes, 0, payload, 1 + langLength, textLength );

        NdefRecord recordNFC = new NdefRecord( NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload );
        return recordNFC;
    }

    private void writeTag( String text, Tag tag ) throws IOException, FormatException
    {
        //Write payload information to the nfc tag.
        NdefRecord[] records = {createRecord( text )};
        NdefMessage message = new NdefMessage( records );
        Ndef ndef = Ndef.get( tag );
        ndef.connect();
        ndef.writeNdefMessage( message );
        ndef.close();
    }

    private void postTask()
    {

        if( _currentFragment == Common.Fragments.SimpleReadFragment.ID )
        {
            new AlertDialog.Builder( this )
                    .setTitle( R.string.app_name )
                    .setMessage( getString( R.string.str_simple_nfc_message ) + _result )
                    .setPositiveButton( R.string.str_cool, new DialogInterface.OnClickListener()
                    {
                        public void onClick( DialogInterface dialog, int which )
                        {
                            //In case you didn't notice, these both do exactly the same thing ;)

                        }
                    } )
                    .setNegativeButton( R.string.str_awesome, new DialogInterface.OnClickListener()
                    {
                        public void onClick( DialogInterface dialog, int which )
                        {
                            // do nothing
                        }
                    } )
                    .setIcon( R.drawable.ic_action_cast )
                    .show();
        }
        else if( _currentFragment == Common.Fragments.SimpleWriteFragment.ID )
        {
            try
            {
                if( _tag == null )
                {
                    Toast.makeText( this, getString( R.string.str_msg_null), Toast.LENGTH_LONG ).show();
                }
                else if( _simpleWriteFragment.getMessage().toString().equals( "" ) )
                {
                    Toast.makeText( this, getString( R.string.str_msg_empty), Toast.LENGTH_LONG ).show();
                }
                else
                {
                    writeTag( _simpleWriteFragment.getMessage().toString(), _tag );
                    Toast.makeText( this, getString( R.string.str_msg_success), Toast.LENGTH_LONG ).show();
                }
            }
            catch( IOException e )
            {
                Toast.makeText( this, getString( R.string.str_msg_ioexception), Toast.LENGTH_LONG ).show();
                e.printStackTrace();
            }
            catch( FormatException e )
            {
                Toast.makeText( this, getString( R.string.str_msg_formatexception), Toast.LENGTH_LONG ).show();
                e.printStackTrace();
            }
        }
    }

    //Inner Class for AsyncTask
    private class NdefReaderTask extends AsyncTask<Tag, Void, String>
    {

        @Override
        protected String doInBackground( Tag... params )
        {
            Tag tag = params[0];

            Ndef ndef = Ndef.get( tag );
            if( ndef == null )
            {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for( NdefRecord ndefRecord : records )
            {
                if( ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals( ndefRecord.getType(), NdefRecord.RTD_TEXT ) )
                {
                    try
                    {
                        return readText( ndefRecord );
                    }
                    catch( UnsupportedEncodingException e )
                    {
                        Log.e( Common.Activities.MainActivity.TAG, getString( R.string.str_msg_unsupported_encoding), e );
                    }
                }
            }

            return null;
        }

        private String readText( NdefRecord record ) throws UnsupportedEncodingException
        {


            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ( ( payload[0] & 128 ) == 0 ) ? getString( R.string.str_utf8) : getString( R.string.str_utf16);

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // Get the Text and return it.
            return new String( payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding );
        }

        @Override
        protected void onPostExecute( String result )
        {
            if( result != null )
            {

                //Handle what happens if the result isn't null. This MIGHT be different
                //depending on the current fragment we're on, so handle that here.
                _result = result;
                if( _currentFragment == Common.Fragments.KeyGenerateFragment.ID )
                {

                }
                else if( _currentFragment == Common.Fragments.KeyAuthenticateFragment.ID )
                {

                }
                else if( _currentFragment == Common.Fragments.SimpleWriteFragment.ID )
                {
                    postTask();
                }
                else
                {
                    onNavigationDrawerItemSelected( Common.Fragments.SimpleReadFragment.ID );
                    postTask();
                }
            }
        }
        //End Inner Class NdefReaderTask
    }
    //End of My stuff, sam's stuff starts right here.

    @Override
    public void onButtonEncodeClicked(View view)
    {
        TextView TextViewMessage = (TextView) findViewById(R.id.editText_Message);

        //if no key pair found in memory call function to end of catch

        // else get keypair from memory

        if (getKeys())
        {
            byte[] encodedBytes = encodeMessage(TextViewMessage.getText().toString().getBytes());

            TextView TextViewEncoded = (TextView) findViewById(R.id.textView_EncodedMessage);

            try
            {
                // use encode to string to get the byte array to a string for
                String temp = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
                TextViewEncoded.setText("Encoded Message: " + Base64.encodeToString(encodedBytes, Base64.DEFAULT));
                toQRCode(Base64.encodeToString(encodedBytes, Base64.DEFAULT));
            }
            catch (Exception ex)
            {
                TextViewEncoded.setText("Encoded Message: ");
            }
        }
    }

    @Override
    public void onButtonDecodeClicked(View view)
    {
        try
        {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

            startActivityForResult(intent, 0);
        }
        catch (Exception e)
        {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);
        }
    }

    private byte[] decodeMessage(byte[] encodedMessage)
    {
        byte[] decodedBytes = null;
        try
        {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.DECRYPT_MODE, m_publicKey);
            decodedBytes = c.doFinal(encodedMessage);
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Couldn't Decrypt with RSA", Toast.LENGTH_SHORT).show();
        }

        return decodedBytes;
    }

    private byte[] encodeMessage(byte[] plainMessage)
    {
        byte[] encodedBytes = null;

        try
        {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.ENCRYPT_MODE, m_privateKey);
            encodedBytes = c.doFinal(plainMessage);

        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Couldn't Encrypt with RSA", Toast.LENGTH_SHORT).show();
        }

        return encodedBytes;
    }

    private boolean getKeys()
    {
        if (m_keyPairGenerated)
        {
            if (m_publicKey == null || m_privateKey == null)
            {
                readKeys(getApplicationContext());
            }

            return true;
        }
        else
        {
            if (m_publicKey == null || m_privateKey == null)
            {
                try
                {
                    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                    kpg.initialize(m_keyLength);
                    m_keyPair = kpg.genKeyPair();
                    m_publicKey = m_keyPair.getPublic();
                    m_privateKey = m_keyPair.getPrivate();

                    m_keyPairGenerated = true;

                    writeKeys(getApplicationContext());

                    return true;
                }
                catch (Exception ex)
                {
                    Toast.makeText(this, "Couldn't Generate Key Pair", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            return true;
        }
    }

    private Boolean readKeys(Context context)
    {
        byte [] publicBytes;
        byte [] privateBytes;

        KeyFactory keyFactory;

        try
        {
            keyFactory = KeyFactory.getInstance("RSA");
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Couldn't get instance of key factory", Toast.LENGTH_SHORT).show();
            return false;
        }

        try
        {
            FileInputStream fis = context.openFileInput(m_publicFileName);
            publicBytes = new byte[m_keyLength];
            fis.read(publicBytes);
            fis.close();
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Couldn't Open Public Input File" + ex, Toast.LENGTH_SHORT).show();
            return false;
        }
        try
        {
            FileInputStream fis = openFileInput(m_privateFileName);
            privateBytes = new byte[m_keyLength];
            fis.read(privateBytes);
            fis.close();
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Couldn't Open Private Input File", Toast.LENGTH_SHORT).show();
            return false;
        }

        try
        {
            X509EncodedKeySpec encodedPubSpec = new X509EncodedKeySpec(publicBytes);
            m_publicKey = keyFactory.generatePublic(encodedPubSpec);

            PKCS8EncodedKeySpec encodedPrivSpec = new PKCS8EncodedKeySpec(privateBytes);
            m_privateKey = keyFactory.generatePrivate(encodedPrivSpec);

            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    private  void toQRCode (String message)
    {
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3/4;

        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(message,
                null,
                Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(),
                smallerDimension);
        try
        {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            ImageView myImage = (ImageView) findViewById(R.id.imageView_QRCode);
            myImage.setImageBitmap(bitmap);

        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }
    }

    private void writeKeys(Context context)
    {
        Boolean publicWritten = false;
        Boolean privateWritten = false;

        try
        {
            X509EncodedKeySpec encodedPubSpec = new X509EncodedKeySpec(m_publicKey.getEncoded());

            FileOutputStream fos = context.openFileOutput(m_publicFileName, Context.MODE_PRIVATE);
            fos.write(encodedPubSpec.getEncoded());
            fos.close();

            publicWritten = true;
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Couldn't open output file", Toast.LENGTH_SHORT).show();
        }
        try
        {
            PKCS8EncodedKeySpec encodedPrivSpec = new PKCS8EncodedKeySpec(m_privateKey.getEncoded());

            FileOutputStream fos = openFileOutput(m_privateFileName, Context.MODE_PRIVATE);
            fos.write(encodedPrivSpec.getEncoded());
            fos.close();

            privateWritten = true;
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Couldn't open private output file", Toast.LENGTH_SHORT).show();
        }

        if (publicWritten && privateWritten)
        {
            SharedPreferences settings = getSharedPreferences(KEY_PAIR_GENERATED, MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("keyPairGenerated", true);
            editor.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0)
        {
            if (resultCode == RESULT_OK)
            {
                String encodedMessage = data.getStringExtra("SCAN_RESULT");

                byte[] decodedBytes = decodeMessage(Base64.decode(encodedMessage, Base64.DEFAULT));
                TextView TextViewDecoded = (TextView) findViewById(R.id.textView_DecodedMessage);
                TextViewDecoded.setText("Decoded Message: " + new String(decodedBytes));
            }
        }
        if(resultCode == RESULT_CANCELED)
        {
            Toast.makeText(this, "Couldn't Read QR Code", Toast.LENGTH_SHORT).show();
            //handle cancel
        }
    }

    public String keyToString ()
    {
        try
        {
            KeyFactory fact = KeyFactory.getInstance("RSA");

            X509EncodedKeySpec spec = fact.getKeySpec(m_publicKey, X509EncodedKeySpec.class);
            String stringKey = Base64.encodeToString(spec.getEncoded(), Base64.DEFAULT);

            return stringKey;
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Couldn't generate string from key", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void stringToKey (String stringKey)
    {
        try
        {
            KeyFactory fact = KeyFactory.getInstance("RSA");

            byte[] encodedKey = Base64.decode(stringKey, Base64.DEFAULT);
            X509EncodedKeySpec encodedPubSpec = new X509EncodedKeySpec(encodedKey);
            m_publicKey = fact.generatePublic(encodedPubSpec);
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Couldn't generate key from string", Toast.LENGTH_SHORT).show();
        }
    }
    //End of Sam's stuff and file.
}
