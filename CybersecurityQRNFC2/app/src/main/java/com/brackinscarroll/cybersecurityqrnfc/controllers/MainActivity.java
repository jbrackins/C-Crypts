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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

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


public class MainActivity extends ActionBarActivity implements MainFragmentListener
{

    public static final String MIME_TEXT_PLAIN = "text/plain";

    private NfcAdapter _nfcAdapter;
    private ListView _listview;

    Tag mytag;

    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    private String _result;
    private MainFragment _mainFragment;
    private KeyGenerateFragment _keyGenerateFragment;
    private KeyAuthenticateFragment _keyAuthenticateFragment;
    private SimpleReadFragment _simpleReadFragment;
    private SimpleWriteFragment _simpleWriteFragment;

    private MainNavigationDrawerFragment _navigationDrawerFragment;

    private int _currentFragment = -1;

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
        _nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (_nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText( this, "This device doesn't support NFC.", Toast.LENGTH_LONG ).show();
            finish();
            return;

        }

        if (!_nfcAdapter.isEnabled()) {
            //mTextView.setText("NFC is disabled.");
        } else {
            //mTextView.setText(R.string.explanation);
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory( Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };

        handleIntent(getIntent());
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
    public void onResume()
    {
        super.onResume();
        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch( this, _nfcAdapter );
        //WriteModeOn();

    }

    @Override
    public void onPause()
    {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch( this, _nfcAdapter );

        super.onPause();
        //WriteModeOff();

    }


    @Override
    public void onNavigationDrawerItemSelected( int position )
    {
// update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        _currentFragment = position;
        Log.d( Common.Activities.MainActivity.TAG,
                Common.Activities.MainActivity.ON_NAV_DRAWER + " | Position: " + position );
        switch( position )
        {
            case Common.Fragments.MainFragment.ID:
            {
                //Force userParcel to null so that your profile shows up.
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
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Toast.makeText(this, "Hella", Toast.LENGTH_LONG ).show();
        }
        handleIntent(intent);
    }


    /**
     * @param activity The corresponding {@link android.app.Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity( activity.getApplicationContext(), 0, intent, 0 );

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }



    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {
                Log.d( Common.Activities.MainActivity.TAG, "Wrong mime type: " + type );
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
        else if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)){
            mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Toast.makeText(this, "Hella", Toast.LENGTH_LONG ).show();
        }
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String>
    {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals( ndefRecord.getType(), NdefRecord.RTD_TEXT )) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(Common.Activities.MainActivity.TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {

                _result = result;
                if( _currentFragment == Common.Fragments.KeyGenerateFragment.ID )
                {

                }
                else if ( _currentFragment == Common.Fragments.KeyAuthenticateFragment.ID )
                {

                }
                else if ( _currentFragment == Common.Fragments.SimpleWriteFragment.ID )
                {
                    showResult();
                }
                else
                {
                    onNavigationDrawerItemSelected( Common.Fragments.SimpleReadFragment.ID );
                    showResult();
                }

                //mTextView.setText("Read content: " + result);
            }
        }
    }

    private void showResult()
    {

        if( _currentFragment == Common.Fragments.SimpleReadFragment.ID )
        {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(getString( R.string.str_simple_nfc_message )  + _result)
                    .setPositiveButton(R.string.str_cool, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //In case you didn't notice, these both do exactly the same thing ;)

                        }
                    })
                    .setNegativeButton(R.string.str_awesome, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon( R.drawable.ic_action_cast )
                    .show();
        }
        else if ( _currentFragment == Common.Fragments.SimpleWriteFragment.ID )
        {
            try {
                if(mytag==null){
                    Toast.makeText( this, "Null Tag", Toast.LENGTH_LONG ).show();
                }else{
                    write(_simpleWriteFragment.getMessage(),mytag);
                    Toast.makeText( this, "Message Written to NFC", Toast.LENGTH_LONG ).show();
                }
            } catch (IOException e) {
                Toast.makeText( this, "Something REALLY messed up", Toast.LENGTH_LONG ).show();
                e.printStackTrace();
            } catch (FormatException e) {
                Toast.makeText( this, "This device doesn't support NFC.", Toast.LENGTH_LONG ).show();
                e.printStackTrace();
            }
        }
    }



    private NdefRecord createRecord(String text) throws UnsupportedEncodingException
    {

        //create the message in according with the standard
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;

        byte[] payload = new byte[1 + langLength + textLength];
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
        return recordNFC;
    }

    private void write(String text, Tag tag) throws IOException, FormatException
    {

        NdefRecord[] records = { createRecord(text) };
        NdefMessage message = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }


}
