package com.brackinscarroll.cybersecurityqrnfc.views;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brackinscarroll.cybersecurityqrnfc.R;
import com.brackinscarroll.cybersecurityqrnfc.interfaces.MainFragmentListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class KeyGenerateFragment extends Fragment
{

    private MainFragmentListener _listener;

    public KeyGenerateFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState )
    {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_key_generate, container, false );
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


}
