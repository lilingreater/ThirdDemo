package com.example.mycrashtest;

import android.app.Application;
import android.content.Context;

public class MyAppcation extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        CrashHandler.getInstance().init(this);
    }

    public static Context getmContext() {
        return mContext;
    }

}
