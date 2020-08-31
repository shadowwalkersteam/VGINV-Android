package com.techno.vginv;

import android.app.Application;

import com.onesignal.OneSignal;
import com.techno.vginv.utils.SharedPrefManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPrefManager.init(getApplicationContext());
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
}
