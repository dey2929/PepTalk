package com.aset.dey.peptalk;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by dey on 15-Jul-15.
 */
public class PepTalkApplication extends Application {

    @Override

    public void onCreate()
    {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "iCTw7Vxhhr0l3pnsnir5M7ysldGCN4gnVKCRSHDg", "FGnhTLoPWSnObdwawznTbtTon8nfCobgemZRrBc2");//the first is the device app id and the second is the backend id

    }
}
