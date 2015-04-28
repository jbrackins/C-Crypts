package com.brackinscarroll.cybersecurityqrnfc.controllers;

import android.app.FragmentManager;
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
import com.brackinscarroll.cybersecurityqrnfc.interfaces.MainFragmentListener;
import com.brackinscarroll.cybersecurityqrnfc.views.MainFragment;

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

    private MainFragment _mainFragment;
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        if( savedInstanceState == null )
        {
            FragmentManager fragmentManager = getFragmentManager();

            _mainFragment = new MainFragment();
            fragmentManager.beginTransaction()
                    .add( R.id.container, _mainFragment )
                    .commit();
        }
        else
        {

        }

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
    }

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
}
