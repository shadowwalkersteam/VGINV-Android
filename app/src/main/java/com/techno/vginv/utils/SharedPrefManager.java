package com.techno.vginv.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static SharedPreferences mSharedPref;
    public static final String TOKEN = "TOKEN";
    public static final String IS_LOGGEDIN = "IS_LOGGEDIN";
    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_DESIGNATION = "USER_DESIGNATION";
    public static final String USER_PORIFLE_PIC = "USER_PORIFLE_PIC";
    public static final String USER_TYPE = "USER_TYPE";
    public static final String LOGGED_IN_USER_TYPE = "USER_LOGGED_IN";
    public static final String TOGGLER_USER_TYPE = "TOGGLER_USER_TYPE";
    public static final String EMAIL = "EMAIL";
    public static final String PASSWORD = "PASSWORD";
    public static final String LANGUAGE = "LANGUAGE";
    public static final String SWITCH_POPUP = "SWITCH_POPUP";
    public static final String UNREAD_COUNTS = "UNREAD_COUNTS";
    public static final String MESSAGE_TIME = "MESSAGE_TIME";
    public static final String SESSION_TIME = "SESSION_TIME";


    private SharedPrefManager() {

    }

    public static void init(Context context) {
        if(mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).commit();
    }

    public static void write(String key, Long value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putLong(key, value).commit();
    }

    public static long read(String key, Long defValue) {
        return mSharedPref.getLong(key, defValue);
    }

    public static void delete() {
        mSharedPref.edit().clear().apply();
    }
}
