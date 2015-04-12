package com.brackinscarroll.cybersecurityqrnfc.controllers;

import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.brackinscarroll.cybersecurityqrnfc.R;
import com.brackinscarroll.cybersecurityqrnfc.interfaces.MainFragmentListener;
import com.brackinscarroll.cybersecurityqrnfc.views.MainFragment;


public class MainActivity extends ActionBarActivity implements MainFragmentListener
{

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
}
