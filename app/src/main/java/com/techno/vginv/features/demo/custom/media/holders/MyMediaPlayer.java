package com.techno.vginv.features.demo.custom.media.holders;

import android.content.Context;
import android.media.MediaPlayer;

public class MyMediaPlayer {
    static MediaPlayer mediaPlayer;
    private static MyMediaPlayer ourInstance = new MyMediaPlayer();
    private Context appContext;

    private MyMediaPlayer() {

    }

    public static Context get() {
        return getInstance().getContext();
    }

    public static synchronized MyMediaPlayer getInstance() {
        return ourInstance;
    }

    public void init(Context context) {
        if (appContext == null) {
            this.appContext = context;
        }
    }

    private Context getContext() {
        return appContext;
    }

    public static MediaPlayer getSingletonMedia() {
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
        return mediaPlayer;
    }
}
