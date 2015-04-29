package com.brackinscarroll.cybersecurityqrnfc.views;


import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brackinscarroll.cybersecurityqrnfc.R;
import com.brackinscarroll.cybersecurityqrnfc.common.Common;
import com.brackinscarroll.cybersecurityqrnfc.interfaces.MainFragmentListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class SimpleReadFragment extends Fragment
{


    private MainFragmentListener _listener;
    private TextView _textViewResult;

    public SimpleReadFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState )
    {
        View rootView = inflater.inflate( R.layout.fragment_simple_read, container, false );

        _textViewResult = ( TextView ) rootView.findViewById( R.id.text_view_simple_read_result );
        // Inflate the layout for this fragment
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


    public void setResult( String result )
    {
        //whatever, this isn't working :(
    }
}
