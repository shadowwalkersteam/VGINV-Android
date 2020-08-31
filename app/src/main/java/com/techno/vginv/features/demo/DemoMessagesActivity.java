package com.techno.vginv.features.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.R;
import com.techno.vginv.data.fixtures.MessagesFixtures;
import com.techno.vginv.data.model.Message;
import com.techno.vginv.features.demo.def.DefaultDialogsActivity;
import com.techno.vginv.features.demo.def.DefaultMessagesActivity;
import com.techno.vginv.utils.AppUtils;

import java.security.spec.ECField;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;

/*
 * Created by troy379 on 04.04.17.
 */
public abstract class DemoMessagesActivity extends AppCompatActivity
        implements MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener {

    private static final int TOTAL_MESSAGES_COUNT = 100;

    protected final String senderId = "0";
    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;
    public final int[] size = {0};

    private Menu menu;
    private int selectionCount;
    public long lastLoadedDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                if (url.contains("video") || url.endsWith(".mp4") || url.endsWith(".mkv") || url.endsWith("flv")) {
                    new Thread(() -> {
                        try {
                            Bitmap bitmap = null;
                            if (url.startsWith("content://")) {
                                runOnUiThread(() -> {
                                    Glide.with(DemoMessagesActivity.this)
                                            .asBitmap()
                                            .load(url)
                                            .into(new CustomTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                    Bitmap smallImage = BitmapFactory.decodeResource(getResources(), R.drawable.play_button);
                                                    smallImage = Bitmap.createScaledBitmap(smallImage, 350, 350, false);
                                                    Bitmap mergedImages = createSingleImageFromMultipleImages(resource, smallImage);
                                                    imageView.setImageBitmap(mergedImages);
                                                }

                                                @Override
                                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                                }
                                            });
                                });
                            } else {
                                try {
                                    bitmap = retriveVideoFrameFromVideo(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + url);
                                    if (bitmap != null) {
                                        Bitmap smallImage = BitmapFactory.decodeResource(getResources(), R.drawable.play_button);
                                        smallImage = Bitmap.createScaledBitmap(smallImage, 350, 350, false);
                                        bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);
                                        Bitmap mergedImages = createSingleImageFromMultipleImages(bitmap, smallImage);
                                        runOnUiThread(() -> {
                                            imageView.setImageBitmap(mergedImages);
//                                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.layer));
                                        });
                                    }
                                } catch (Throwable e) {

                                }
                            }
                        } catch (Exception throwable) {
                            throwable.printStackTrace();
                        }
                    }).start();

                    System.out.println("This is video");
                } else if (url.startsWith("content://")) {
//                    Picasso.get().load(url).fit().centerCrop().into(imageView);
                    Glide.with(getApplicationContext().getApplicationContext())
                        .load(url)
                        .into(imageView);
                } else {
//                    Picasso.get().load(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + url).into(imageView);
                    if (url.startsWith(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL)) {
                        Glide.with(getApplicationContext().getApplicationContext())
                                .load(url)
                                .fitCenter()
                                .override(1000,1000)
                                .into(imageView);
                    } else {
                        Glide.with(getApplicationContext().getApplicationContext())
                                .load(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + url)
                                .fitCenter()
                                .override(1000,1000)
                                .into(imageView);
                    }
//                    Picasso.get().load(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + url).into(imageView);
                }
//                imageView.setImageResource(R.drawable.sarah);
            }
        };
        loadMessages();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        messagesAdapter.addToStart(DefaultMessagesActivity.dialogModel.getLastMessage(), true);
    }

    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage){
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        int width = firstImage.getWidth();
        int height = firstImage.getHeight();
        float centerX = (width  - secondImage.getWidth()) * 0.5f;
        float centerY = (height- secondImage.getHeight()) * 0.5f;
        canvas.drawBitmap(secondImage, centerX, centerY, null);
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.chat_actions_menu, menu);
        onSelectionChanged(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                messagesAdapter.deleteSelectedMessages();
                break;
            case R.id.action_copy:
                messagesAdapter.copySelectedMessagesText(this, getMessageStringFormatter(), true);
                AppUtils.showToast(this, R.string.copied_message, true);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed();
        } else {
            messagesAdapter.unselectAllItems();
        }
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        Log.i("TAG", "onLoadMore: " + page + " " + totalItemsCount);
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
//            loadMessages();
        }
    }

    @Override
    public void onSelectionChanged(int count) {
        this.selectionCount = count;
        menu.findItem(R.id.action_delete).setVisible(count > 0);
        menu.findItem(R.id.action_copy).setVisible(count > 0);
    }

    protected void loadMessages() {
        new Handler().postDelayed(new Runnable() { //imitation of internet connection
            @Override
            public void run() {
                if (DefaultMessagesActivity.isRoomChat) {
                    try {
                        ArrayList<Message> messages = MessagesFixtures.getRoomMessages();
                        lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt().getTime();
                        messagesAdapter.addToEnd(messages, true);
                        size[0] = messages.size();
                    } catch (Exception e) {

                    }
                } else if (DefaultDialogsActivity.isGroupChat) {
                    try {
                        ArrayList<Message> messages = MessagesFixtures.getMessages();
                        lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt().getTime();
                        messagesAdapter.addToEnd(messages, true);
                        size[0] = messages.size();
                    } catch (Exception e) {

                    }
                } else {
                    try {
                        System.out.println("Load messages called");
                        ArrayList<Message> messages = MessagesFixtures.getMessages();
                        lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt().getTime();
                        messagesAdapter.addToEnd(messages, true);
                        size[0] = messages.size();
                    } catch (Exception e) {

                    }
                }
            }
        }, 2000);
    }

    private MessagesListAdapter.Formatter<Message> getMessageStringFormatter() {
        return new MessagesListAdapter.Formatter<Message>() {
            @Override
            public String format(Message message) {
                String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                        .format(message.getCreatedAt());

                String text = message.getText();
                if (text == null) text = "[attachment]";

                return String.format(Locale.getDefault(), "%s: %s (%s)",
                        message.getUser().getName(), text, createdAt);
            }
        };
    }

    public Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    public Bitmap retriveVideoFrameFromVideo2(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
}
