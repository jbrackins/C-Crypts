package com.brackinscarroll.cybersecurityqrnfc.views;


import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.brackinscarroll.cybersecurityqrnfc.R;
import com.brackinscarroll.cybersecurityqrnfc.common.Common;
import com.brackinscarroll.cybersecurityqrnfc.interfaces.MainFragmentListener;


/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class MainNavigationDrawerFragment extends Fragment
{


    private MainFragmentListener _listener;
    private ActionBarDrawerToggle _actionBarDrawerToggle;

    private DrawerLayout _drawerLayout;
    private ListView _drawerListView;
    private View _containerView;

    private int _currentSelectedPosition = 0;
    private boolean _fromSavedInstanceState;
    private boolean _userLearnedDrawer;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( getActivity() );
        _userLearnedDrawer = sharedPreferences.getBoolean( Common.Fragments.Navigation.PREF_USER_LEARNED_DRAWER, false );

        if( savedInstanceState != null )
        {
            _currentSelectedPosition = savedInstanceState.getInt( Common.Fragments.Navigation.STATE_SELECTED_POSITION );
            _fromSavedInstanceState = true;
        }

        if( savedInstanceState == null )
        {
            // Select either the default item (0) or the last selected item.
            selectItem( _currentSelectedPosition );
        }
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState )
    {
        super.onActivityCreated( savedInstanceState );

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu( true );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState )
    {
        _drawerListView = ( ListView ) inflater.inflate( R.layout.fragment_main_navigation_drawer, container, false );
        _drawerListView.setOnItemClickListener( new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id )
            {
                selectItem( position );
            }
        } );
        return _drawerListView;
    }

    public boolean isDrawerOpen()
    {
        return _drawerLayout != null && _drawerLayout.isDrawerOpen( _containerView );
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp( int fragmentId, DrawerLayout drawerLayout )
    {

            _drawerListView.setAdapter( new ArrayAdapter<String>( getActionBar().getThemedContext(),
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text1,
                    new String[]{
                            getString( R.string.title_frag_1 ),
                            getString( R.string.title_frag_2 ),
                            getString( R.string.title_frag_3 ),
                            getString( R.string.title_frag_4 ),
                            getString( R.string.title_frag_5 ),
                    } ) );

        _drawerListView.setItemChecked( _currentSelectedPosition, true );

        _containerView = getActivity().findViewById( fragmentId );
        _drawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        // _drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // set up the drawer's list view with items and click listener
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled( true );
        actionBar.setHomeButtonEnabled( true );

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        _actionBarDrawerToggle = new ActionBarDrawerToggle( getActivity(), _drawerLayout, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close )
        {
            @Override
            public void onDrawerClosed( View drawerView )
            {
                super.onDrawerClosed( drawerView );
                if( isAdded() == false )
                {
                    return;
                }

                // calls onPrepareOptionsMenu()
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened( View drawerView )
            {
                super.onDrawerOpened( drawerView );
                if( isAdded() == false )
                {
                    return;
                }

                if( _userLearnedDrawer == false )
                {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    _userLearnedDrawer = true;
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( getActivity() );
                    sharedPreferences.edit().putBoolean( Common.Fragments.Navigation.PREF_USER_LEARNED_DRAWER, true ).apply();
                }

                // calls onPrepareOptionsMenu()
                getActivity().invalidateOptionsMenu();
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if( _userLearnedDrawer == false && _fromSavedInstanceState == false )
        {
            _drawerLayout.openDrawer( _containerView );
        }

        // Defer code dependent on restoration of previous instance state.
        _drawerLayout.post( new Runnable()
        {
            @Override
            public void run()
            {
                _actionBarDrawerToggle.syncState();
            }
        } );

        _drawerLayout.setDrawerListener( _actionBarDrawerToggle );
    }

    private void selectItem( int position )
    {
        _currentSelectedPosition = position;

        if( _drawerListView != null )
        {
            _drawerListView.setItemChecked( position, true );
            _drawerLayout.closeDrawer( _containerView );
        }

        if( _listener != null )
        {
            _listener.onNavigationDrawerItemSelected( position );
        }
    }

    @Override
    public void onAttach( Activity activity )
    {
        super.onAttach( activity );
        try
        {
            _listener = ( MainFragmentListener ) activity;
        }
        catch( ClassCastException e )
        {
            throw new ClassCastException( "Activity must implement IFragmentListener." );
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        _listener = null;
    }

    @Override
    public void onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState( outState );
        outState.putInt( Common.Fragments.Navigation.STATE_SELECTED_POSITION, _currentSelectedPosition );
    }

    @Override
    public void onConfigurationChanged( Configuration newConfig )
    {
        super.onConfigurationChanged( newConfig );

        // Forward the new configuration the drawer toggle component.
        _actionBarDrawerToggle.onConfigurationChanged( newConfig );
    }

    @Override
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater )
    {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if( _drawerLayout != null && isDrawerOpen() == true )
        {
            inflater.inflate( R.menu.menu_main, menu );
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu( menu, inflater );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        // If the Action Bar Drawer Toggle handled the menu selection.
        if( _actionBarDrawerToggle.onOptionsItemSelected( item ) == true )
        {
            // If options menu item was handled, return the same value.
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar()
    {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled( true );
        actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_STANDARD );
        actionBar.setTitle( R.string.app_name );
    }

    private ActionBar getActionBar()
    {
        return ( ( ActionBarActivity ) getActivity() ).getSupportActionBar();
    }

}
