package com.brackinscarroll.cybersecurityqrnfc.controllers;

import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;

import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.Key;
import javax.crypto.Cipher;

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

    @Override
    public void onButtonEncodeClicked(View view)
    {
        TextView TextViewMessage = (TextView) findViewById(R.id.editText_Message);

        Key publicKey = null;
        Key privateKey = null;

        //if no key pair found in memory call function to end of catch
        try
        {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp = kpg.genKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Couldn't Generate Key Pair", Toast.LENGTH_SHORT).show();
        }
        // else get keypair from memory

        // call when we send the message, send with public key
        byte[] encodedBytes = null;
        try
        {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.ENCRYPT_MODE, privateKey);
            encodedBytes = c.doFinal(TextViewMessage.getText().toString().getBytes());
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Couldn't Encrypt with RSA", Toast.LENGTH_SHORT).show();
        }
        // send encoded bytes

        TextView TextViewEncoded = (TextView)findViewById(R.id.textView_EncodedMessage);
        try
        {
            TextViewEncoded.setText("Encoded Message: " + Base64.encodeToString(encodedBytes, Base64.DEFAULT));
        }
        catch (Exception ex)
        {
            TextViewEncoded.setText("Encoded Message: ");
        }

        byte[] decodedBytes = null;
        try
        {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.DECRYPT_MODE, publicKey);
            decodedBytes = c.doFinal(encodedBytes);
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Couldn't Encrypt with RSA", Toast.LENGTH_SHORT).show();
        }

        TextView TextViewDecoded = (TextView)findViewById(R.id.textView_DecodedMessage);
        try
        {
            TextViewDecoded.setText("Decoded Message: " + new String(decodedBytes));
        }
        catch (Exception ex)
        {
            TextViewDecoded.setText("Decoded Message: ");
        }
    }
}
