package com.brackinscarroll.cybersecurityqrnfc.views;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.brackinscarroll.cybersecurityqrnfc.R;
import com.brackinscarroll.cybersecurityqrnfc.interfaces.MainFragmentListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class SimpleWriteFragment extends Fragment implements View.OnClickListener
{

    private MainFragmentListener _listener;

    private EditText _editTextNFCMessage;
    private Button _buttonClear;

    public SimpleWriteFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState )
    {
        View rootView = inflater.inflate( R.layout.fragment_simple_write, container, false );
        // Inflate the layout for this fragment

        _editTextNFCMessage = ( EditText ) rootView.findViewById( R.id.edit_text_simple_write_message );
        _editTextNFCMessage.setText( "" );

        _buttonClear = ( Button ) rootView.findViewById( R.id.btn_clear );
        _buttonClear.setOnClickListener( this );
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

        switch( button.getId() ) //button.getText().toString().toLowerCase() )
        {
            case R.id.btn_clear:
                _editTextNFCMessage.setText( "" );
                break;

        }
    }

    public String getMessage()
    {
        if( _editTextNFCMessage!= null )
            return _editTextNFCMessage.toString();
        else
            return "";
    }
}
