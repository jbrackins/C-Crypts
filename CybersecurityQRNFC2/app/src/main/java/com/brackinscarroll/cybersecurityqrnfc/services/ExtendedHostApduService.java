package com.brackinscarroll.cybersecurityqrnfc.services;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import com.brackinscarroll.cybersecurityqrnfc.common.Common;

/**
 * Created by Julian on 4/12/2015.
 */
public class ExtendedHostApduService extends HostApduService
{
    private int _messageCount = 0;

    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        if (selectAidApdu(apdu)) {
            Log.d( Common.Services.ExtendedHostApduService.TAG,
                   Common.Services.ExtendedHostApduService.SELECT );
            return getWelcomeMessage();
        }
        else {
            Log.d(Common.Services.ExtendedHostApduService.TAG,
                  Common.Services.ExtendedHostApduService.SELECT + new String( apdu ) );
            return getNextMessage();
        }
    }

    private byte[] getWelcomeMessage() {
        return Common.Services.ExtendedHostApduService.DESKTOP_MESSAGE.getBytes();
    }

    private byte[] getNextMessage() {
        return (Common.Services.ExtendedHostApduService.ANDROID_MESSAGE + _messageCount++).getBytes();
    }

    private boolean selectAidApdu(byte[] apdu) {
        return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
    }

    @Override
    public void onDeactivated(int reason) {
        Log.d(Common.Services.ExtendedHostApduService.TAG,
              Common.Services.ExtendedHostApduService.DEACTIVATE + reason);
    }
}
