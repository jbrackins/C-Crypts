package com.brackinscarroll.cybersecurityqrnfc.interfaces;

import android.view.View;

/**
 * Created by Julian on 4/12/2015.
 */
public interface MainFragmentListener
{
    public void onNavigationDrawerItemSelected( int position );
    public void aboutApp();
    public void onButtonEncodeClicked (View view);
    public void onButtonDecodeClicked (View view);
}
