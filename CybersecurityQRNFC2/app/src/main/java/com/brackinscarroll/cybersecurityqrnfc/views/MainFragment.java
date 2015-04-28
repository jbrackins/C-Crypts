package com.brackinscarroll.cybersecurityqrnfc.views;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.brackinscarroll.cybersecurityqrnfc.R;
import com.brackinscarroll.cybersecurityqrnfc.adapters.ExtendedAdapter;
import com.brackinscarroll.cybersecurityqrnfc.interfaces.MainFragmentListener;

public class MainFragment extends Fragment implements View.OnClickListener
{

    private MainFragmentListener _listener;

    private Button _buttonAbout;

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
        _buttonAbout = ( Button ) rootView.findViewById( R.id.btn_about );
        _buttonAbout.setOnClickListener( this );
        return rootView;
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
            throw new ClassCastException( activity.toString()
                    + " must implement MainFragmentListener" );
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        _listener = null;
    }

    @Override
    public void onSaveInstanceState( Bundle bundle )
    {
        super.onSaveInstanceState( bundle );
    }

    @Override
    public void onClick( View v )
    {
        Button button = ( Button ) v;

        switch( button.getId() )
        {
            case R.id.btn_about:
                _listener.aboutApp();
                break;

        }
    }
}
