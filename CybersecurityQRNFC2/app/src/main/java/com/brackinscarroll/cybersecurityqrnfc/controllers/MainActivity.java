package com.brackinscarroll.cybersecurityqrnfc.controllers;

import android.app.FragmentManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.brackinscarroll.cybersecurityqrnfc.R;
import com.brackinscarroll.cybersecurityqrnfc.common.Common;
import com.brackinscarroll.cybersecurityqrnfc.interfaces.MainFragmentListener;
import com.brackinscarroll.cybersecurityqrnfc.interfaces.MessageListener;
import com.brackinscarroll.cybersecurityqrnfc.models.IsoDepRunnable;
import com.brackinscarroll.cybersecurityqrnfc.views.KeyAuthenticateFragment;
import com.brackinscarroll.cybersecurityqrnfc.views.KeyGenerateFragment;
import com.brackinscarroll.cybersecurityqrnfc.views.MainFragment;
import com.brackinscarroll.cybersecurityqrnfc.views.MainNavigationDrawerFragment;
import com.brackinscarroll.cybersecurityqrnfc.views.SimpleReadFragment;
import com.brackinscarroll.cybersecurityqrnfc.views.SimpleWriteFragment;


public class MainActivity extends ActionBarActivity implements MainFragmentListener,
                                                               MessageListener,
                                                               NfcAdapter.ReaderCallback
{

    private NfcAdapter _nfcAdapter;
    private ListView _listview;

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
    public void onResume()
    {
        super.onResume();
        //Resume the NFC reader capabilities.
        _nfcAdapter.enableReaderMode( this, this,
                NfcAdapter.FLAG_READER_NFC_A |
                        NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                null );

    }

    @Override
    public void onPause()
    {
        super.onPause();
        //Stop the NFC reader capabilities.
        _nfcAdapter.disableReaderMode( this );
    }

    @Override
    public void onMessageReceived( final byte[] msg )
    {
        runOnUiThread( new Runnable()
        {
            @Override
            public void run()
            {
                _mainFragment.setAdapterMessage( new String(msg) );
            }
        } );
    }

    @Override
    public void onError( Exception e )
    {
        Log.d( Common.Activities.MainActivity.TAG,
                Common.Activities.MainActivity.ON_ERROR );
        this.onMessageReceived( e.getMessage().getBytes() );
    }

    @Override
    public void onTagDiscovered( Tag tag )
    {
        Log.d( Common.Activities.MainActivity.TAG,
                Common.Activities.MainActivity.ON_TAG_DISCOVERED );
        IsoDep iso = IsoDep.get(tag);
        if(iso != null)
        {
            IsoDepRunnable isoWorker = new IsoDepRunnable( iso, this );
            Thread thread = new Thread( isoWorker );
            thread.start();
        }
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
            case 0:
            {
                //Force userParcel to null so that your profile shows up.
                _mainFragment = new MainFragment();
                fragmentManager.beginTransaction()
                        .replace( R.id.main_activity_frame, _mainFragment, Common.Fragments.MainFragment.TAG )
                        .commit();
                break;
            }
            case 1:
            {

                _keyGenerateFragment = new KeyGenerateFragment();
                fragmentManager.beginTransaction()
                        .replace( R.id.main_activity_frame, _keyGenerateFragment )
                        .commit();
                break;
            }
            case 2:
            {
                _keyAuthenticateFragment = new KeyAuthenticateFragment();
                fragmentManager.beginTransaction()
                        .replace( R.id.main_activity_frame, _keyAuthenticateFragment )
                        .commit();
                break;
            }
            case 3:
            {
                _simpleReadFragment = new SimpleReadFragment();
                fragmentManager.beginTransaction()
                        .replace( R.id.main_activity_frame, _simpleReadFragment, Common.Fragments.SimpleReadFragment.TAG )
                        .commit();
                break;
            }
            case 4:
            {
                _simpleWriteFragment = new SimpleWriteFragment();
                fragmentManager.beginTransaction()
                        .replace( R.id.main_activity_frame, _simpleWriteFragment, Common.Fragments.SimpleWriteFragment.TAG )
                        .commit();
                break;
            }
        }
    }
}
