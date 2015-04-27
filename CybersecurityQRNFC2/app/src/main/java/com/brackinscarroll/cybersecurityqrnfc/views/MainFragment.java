package com.brackinscarroll.cybersecurityqrnfc.views;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.brackinscarroll.cybersecurityqrnfc.R;
import com.brackinscarroll.cybersecurityqrnfc.adapters.ExtendedAdapter;
import com.brackinscarroll.cybersecurityqrnfc.interfaces.MainFragmentListener;

public class MainFragment extends Fragment
{

    private MainFragmentListener mListener;

    private ListView _listView;
    private ExtendedAdapter _adapter;

    public MainFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        if( savedInstanceState == null )
        {
        }
        else
        {
        }
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState )
    {
        if( savedInstanceState == null )
        {
        }
        else
        {
        }

        View rootView = inflater.inflate( R.layout.fragment_main, container, false );
        _listView = ( ListView ) rootView.findViewById( R.id.list_view );
        _adapter = new ExtendedAdapter( inflater );
        _listView.setAdapter( _adapter );
        return rootView;
    }

    @Override
    public void onAttach( Activity activity )
    {
        super.onAttach( activity );
        try
        {
            mListener = ( MainFragmentListener ) activity;
        }
        catch( ClassCastException e )
        {
            throw new ClassCastException( activity.toString()
                    + " must implement MainFragmentListener" );
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState( Bundle bundle )
    {
        super.onSaveInstanceState( bundle );
    }

    public void setAdapterMessage( String msg )
    {
        _adapter.addMessage( msg );
    }
}
