package com.brackinscarroll.cybersecurityqrnfc.common;

import com.brackinscarroll.cybersecurityqrnfc.R;

/**
 * Created by Julian on 4/13/2015.
 */
public class Common
{

    public class Activities
    {
        public class MainActivity
        {
            public final static String TAG = "MainActivity";
            public final static String ON_ERROR = "onError";
            public final static String ON_TAG_DISCOVERED = "onTagDiscovered";
            public final static String ON_NAV_DRAWER = "onNavigationDrawerItemSelected";

            public final static String CURRENT_FRAGMENT = "MainActivityCurrentFragment";

        }
    }

    public class Fragments
    {
        public class MainFragment
        {
            public final static String TAG = "MainFragment";
        }

        public class SimpleReadFragment
        {
            public final static String TAG = "SimpleReadFragment";
        }

        public class SimpleWriteFragment
        {
            public final static String TAG = "SimpleWriteFragment";
        }

        public class KeyAuthenticateFragment
        {
            public final static String TAG = "KeyAuthenticateFragment";
        }

        public class KeyGenerateFragment
        {
            public final static String TAG = "KeyGenerateFragment";
        }


        public class Navigation
        {
            public static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
            public static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
        }

    }

    public class Adapters
    {

        public class ExtendedAdapter
        {
            public final static String TAG = "ExtendedAdapter";
        }

    }

    public class Runnables
    {
        public class isoDepRunnable
        {
            public final static String TAG = "isoDepRunnable";
        }
    }

    public class Services
    {
        public class ExtendedHostApduService
        {
            public final static String TAG = "ExtendedHostApduService";
            public final static String SELECT = "Application Selected";
            public final static String DEACTIVATE = "Deactivated:";
            public final static String RECEIVE = "Received: ";
            public final static String DESKTOP_MESSAGE = "Hello PcDuino!";
            public final static String ANDROID_MESSAGE = "Message from Android: ";
        }
    }

}
