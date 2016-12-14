package com.howshea.artisanmusic.base.baseapp;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

/**
 * Created by haipo on 2016/11/2.
 */

public class BaseApplication extends Application {

    private static BaseApplication baseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
    }

    public static Context getAppContext() {
        return baseApplication;
    }
    public static Resources getAppResources() {
        return baseApplication.getResources();
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
