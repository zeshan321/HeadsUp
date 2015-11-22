package com.zeshanaslam.headsup;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;

public class ParseSetup extends Application {

    Context con;

    @Override
    public void onCreate() {
        super.onCreate();
        con = this;

        com.parse.Parse.enableLocalDatastore(con);
        Parse.initialize(this, con.getResources().getString(R.string.key_1), con.getResources().getString(R.string.key_2));
    }
}
