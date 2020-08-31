package com.techno.vginv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.onesignal.OneSignal;
import com.techno.vginv.Fragments.MainFragment;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 4000; //splash screen will be shown for 4 seconds
    private boolean isSessionExpired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        new Thread(() -> {
            CloudDataService.getCookies();
            checkIfSessionIsExpired();
        }).start();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.bottom_bar));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        try {
//            if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").toLowerCase().equalsIgnoreCase(SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").toLowerCase())) {
//                SharedPrefManager.write(SharedPrefManager.SWITCH_POPUP, false);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if (SharedPrefManager.read(SharedPrefManager.LANGUAGE, 0) == 1) {
            setApplicationLanguage("ar");
        } else {
            setApplicationLanguage("en");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (SharedPrefManager.read(SharedPrefManager.IS_LOGGEDIN, false) && !isSessionExpired) {
                        startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                    } else {
                        SharedPrefManager.delete();
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public void setApplicationLanguage(String language) {
        Locale myLocale = new Locale(language);
        Resources res = this.getResources();
        DisplayMetrics display = res.getDisplayMetrics();
        Configuration configuration = res.getConfiguration();
        configuration.locale = myLocale;
        res.updateConfiguration(configuration, display);
    }

    private void checkIfSessionIsExpired() {
        try {
            CloudDataService.getUserProfile(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject -> {
                try {
                    if (jsonObject != null && jsonObject.has("updated_at")) {
                        String newTime = jsonObject.getString("updated_at");
                        if (!newTime.equalsIgnoreCase(SharedPrefManager.read(SharedPrefManager.SESSION_TIME, ""))) {
                            isSessionExpired = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
