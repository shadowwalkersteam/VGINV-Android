package com.techno.vginv.features.demo.custom.media.holders;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.utils.DateFormatter;
import com.techno.vginv.R;
import com.techno.vginv.data.model.Message;
import com.techno.vginv.features.demo.def.DefaultMessagesActivity;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

/*
 * Created by troy379 on 05.04.17.
 */
public class OutcomingVoiceMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<Message> {

    private TextView tvDuration;
    private TextView tvTime;
    private ImageButton play;
    private SeekBar seekbar;

    private double startTime = 0;
    private double finalTime = 0;
    public static int oneTimeOnly = 0;
    private Handler myHandler = new Handler();


    public OutcomingVoiceMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        tvDuration = (TextView) itemView.findViewById(R.id.duration);
        tvTime = (TextView) itemView.findViewById(R.id.time);
        play =  itemView.findViewById(R.id.play);

        seekbar = itemView.findViewById(R.id.seekBar);
        seekbar.setClickable(false);
//        mediaPlayer.setOnBufferingUpdateListener(this);
//        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (MyMediaPlayer.getSingletonMedia() != null && MyMediaPlayer.getSingletonMedia().isPlaying()) {
                    MyMediaPlayer.getSingletonMedia().seekTo(seekBar.getProgress());
                }
            }
        });

        AtomicReference<String> duration = new AtomicReference<>("");
//        tvDuration.setText(getDuration(message.getVoice().getUrl()));

        new Thread(() -> {
            try {
                if (!message.getVoice().getDuration().isEmpty()) {
                    duration.set(message.getVoice().getDuration());
                } else {
                    duration.set(getDuration(message.getVoice().getUrl()));
                }
                ((Activity)DefaultMessagesActivity.mContext).runOnUiThread(() -> {
                    tvDuration.setText(duration.get());
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();

        Uri myUri = null;
        try {
            tvTime.setText(DateFormatter.format(message.getCreatedAt(), DateFormatter.Template.TIME));
            tvTime.setVisibility(View.GONE);
            myUri = Uri.parse(message.getVoice().getUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            MyMediaPlayer.getSingletonMedia().setDataSource(DefaultMessagesActivity.mContext, myUri);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        Uri finalMyUri = myUri;
        play.setOnClickListener(v -> {
            try {
                if (MyMediaPlayer.getSingletonMedia().isPlaying()) {
                    MyMediaPlayer.getSingletonMedia().stop();
                    play.setImageResource(R.drawable.ic_play_white);
                    myHandler.removeCallbacks(UpdateSongTime);
                    System.out.println("Media player finished playing audio");
                    seekbar.setProgress(0);
                } else {
                    MyMediaPlayer.getSingletonMedia().reset();
                    MyMediaPlayer.getSingletonMedia().setDataSource(DefaultMessagesActivity.mContext, finalMyUri);
                    MyMediaPlayer.getSingletonMedia().setAudioStreamType(AudioManager.STREAM_MUSIC);
                    MyMediaPlayer.getSingletonMedia().setLooping(false);
                    MyMediaPlayer.getSingletonMedia().prepare();
                    MyMediaPlayer.getSingletonMedia().start();

                    MyMediaPlayer.getSingletonMedia().setOnCompletionListener(mp -> {
                        MyMediaPlayer.getSingletonMedia().stop();
                        myHandler.removeCallbacks(UpdateSongTime);
                        System.out.println("Media player finished playing audio");
                        play.setImageResource(R.drawable.ic_play_white);
                        seekbar.setProgress(0);
                    });

                    play.setImageResource(R.drawable.ic_stop_white_24dp);
                    finalTime = MyMediaPlayer.getSingletonMedia().getDuration();
                    startTime = MyMediaPlayer.getSingletonMedia().getCurrentPosition();

//                    if (oneTimeOnly == 0) {
//                        oneTimeOnly = 1;
//                    }
                    seekbar.setMax((int) finalTime);

//                    seekbar.setProgress((int)startTime);
                    myHandler.postDelayed(UpdateSongTime,100);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private String getDuration(String uri) {
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(uri, new HashMap<String, String>());
            String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return formateMilliSeccond(Long.parseLong(durationStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String formateMilliSeccond(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        //      return  String.format("%02d Min, %02d Sec",
        //                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
        //                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
        //                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        // return timer string
        return finalTimerString;
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
//            int currentPosition = MyMediaPlayer.getSingletonMedia().getCurrentPosition();
//            int total = MyMediaPlayer.getSingletonMedia().getDuration();
//            while (MyMediaPlayer.getSingletonMedia() != null && MyMediaPlayer.getSingletonMedia().isPlaying() && currentPosition < total) {
//                try {
//                    Thread.sleep(1000);
//                    currentPosition = MyMediaPlayer.getSingletonMedia().getCurrentPosition();
//                } catch (InterruptedException e) {
//                    return;
//                } catch (Exception e) {
//                    return;
//                }
//
//                seekbar.setProgress(currentPosition);
//
//            }

            startTime = MyMediaPlayer.getSingletonMedia().getCurrentPosition();
            seekbar.setProgress((int)startTime);
            System.out.println(startTime);
            myHandler.postDelayed(this, 100);
        }
    };
}
