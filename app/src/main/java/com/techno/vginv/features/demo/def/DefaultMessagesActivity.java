package com.techno.vginv.features.demo.def;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.google.gson.Gson;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;
import com.onesignal.OneSignal;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.techno.vginv.Adapters.AttachmentsAdapter;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.Fragments.AllUserProfile;
import com.techno.vginv.FullscreenImageView;
import com.techno.vginv.GroupChatSettings;
import com.techno.vginv.GroupSettings;
import com.techno.vginv.Model.AllChats;
import com.techno.vginv.Model.GroupChats;
import com.techno.vginv.Model.Groups;
import com.techno.vginv.Model.Messages;
import com.techno.vginv.R;
import com.techno.vginv.SharedInstance;
import com.techno.vginv.data.fixtures.MessagesFixtures;
import com.techno.vginv.data.model.Dialog;
import com.techno.vginv.data.model.Message;
import com.techno.vginv.features.demo.DemoMessagesActivity;
import com.techno.vginv.features.demo.custom.media.holders.IncomingVoiceMessageViewHolder;
import com.techno.vginv.features.demo.custom.media.holders.MyMediaPlayer;
import com.techno.vginv.features.demo.custom.media.holders.OutcomingVoiceMessageViewHolder;
import com.techno.vginv.utils.AppUtils;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.FilePath;
import com.techno.vginv.utils.SharedPrefManager;
import com.techno.vginv.utils.SimpleResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.internal.platform.Platform;

public class DefaultMessagesActivity extends DemoMessagesActivity
        implements MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageInput.VoiceRecordingListener,
        MessageHolders.ContentChecker<Message>,
        MessageInput.TypingListener,
        PickiTCallbacks {

    public static Dialog dialogModel;
    private Timer timer;
    private SimpleResponse<Uri> simpleResponse;
    String picturePath = "";
    private boolean isFile = false;
    private Toolbar toolbar;
    private TextView textView;
    private static final byte CONTENT_TYPE_VOICE = 1;
    public static Context mContext;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    public static boolean isRoomChat = false;
    private FrameLayout frameLayout;
    private boolean isUploaded = false;
    ProgressDialog pd;
    PickiT pickiT;
    public static ISO8601DateFormat df = new ISO8601DateFormat();

    private final long THIRTY_MINUTES = 1000 * 60 * 30;

    public static void open(Context context, Dialog dialog, boolean roomChat) {
        dialogModel = null;
        dialogModel = dialog;
        mContext = context;
        isRoomChat = roomChat;
        SharedInstance.setUserMessages(new Messages());
        context.startActivity(new Intent(context, DefaultMessagesActivity.class));
    }

    private MessagesList messagesList;

    @Override
    public Resources.Theme getTheme() {
        try {
            Resources.Theme theme = super.getTheme();
            if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
                if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equalsIgnoreCase("vg")) {
                    theme.applyStyle(R.style.AppTheme_NoActionBar, true);
                } else {
                    theme.applyStyle(R.style.AppThemeHMG_NoActionBar, true);
                }
            } else {
                if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg")) {
                    theme.applyStyle(R.style.AppTheme_NoActionBar, true);
                } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("hmg")) {
                    theme.applyStyle(R.style.AppThemeHMG_NoActionBar, true);
                } else {
                    theme.applyStyle(R.style.AppTheme_NoActionBar, true);
                }
            }
            return theme;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // you could also use a switch if you have many themes that could apply
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_messages);
        toolbar = findViewById(R.id.toolbar);
        textView = toolbar.findViewById(R.id.toolbar_title);
        frameLayout = findViewById(R.id.container);

        if (DefaultDialogsActivity.isGroupChat) {
            textView.setText("All Members");
        } else {
            textView.setText(dialogModel.getDialogName());
        }
        pd = new ProgressDialog(DefaultMessagesActivity.this);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        this.messagesList = (MessagesList) findViewById(R.id.messagesList);
        initAdapter();
        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);
        input.setTypingListener(this);
        input.setAttachmentsListener(this);
        input.setVoiceRecordingListener(this);
        setTitle(dialogModel.getDialogName());
        MyMediaPlayer.getInstance().init(this);

        pickiT = new PickiT(this, this, this);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.mp3";
        myAudioRecorder = new MediaRecorder();

        textView.setOnClickListener(v -> {
            if (isRoomChat) {
                GroupSettings.open(DefaultMessagesActivity.this, dialogModel.getId());
            }
            else if (DefaultDialogsActivity.isGroupChat) {
//                GroupChatSettings.open(DefaultMessagesActivity.this, dialogModel.getId());
            }
            else {
                frameLayout.setVisibility(View.VISIBLE);
                goToFragment(new AllUserProfile(dialogModel.getUsers().get(0).getId(), frameLayout), false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        frameLayout.setVisibility(View.INVISIBLE);
        if (GroupSettings.isGroupDeleted) {
            GroupSettings.isGroupDeleted = false;
            finish();
            return;
        }
        if (isRoomChat) {
            System.out.println("This is room chat");
            SharedInstance.getGroups().getGroupsData().clear();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        CloudDataService.getRoomMessages(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), dialogModel.getId(), s -> {
                            SharedInstance.setGroups(new Gson().fromJson(s, Groups.class));
                            runOnUiThread(() -> {
                                ArrayList<Message> messages = MessagesFixtures.getRoomMessages();
                                if (size[0] > 0 && messages.size() > size[0]) {
                                    try {
                                        if (pd.isShowing()) {
                                            pd.dismiss();
                                        }
                                    } catch (Exception e) {

                                    }
                                    size[0] = messages.size();
                                    messagesAdapter.addToStart(messages.get(messages.size() - 1), true);
                                }
                            });
                            return null;
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 2000);
        } else if (DefaultDialogsActivity.isGroupChat) {
            System.out.println("This is group chat");
            SharedInstance.getGroups().getGroupsData().clear();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        CloudDataService.getAllGroupChats(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), s -> {
                            SharedInstance.setGroups(new Gson().fromJson(s, Groups.class));
                            runOnUiThread(() -> {
                                ArrayList<Message> messages = MessagesFixtures.getMessages();
                                if (size[0] > 0 && messages.size() > size[0]) {
                                    try {
                                        if (pd.isShowing()) {
                                            pd.dismiss();
                                        }
                                    } catch (Exception e) {

                                    }
                                    size[0] = messages.size();
                                    messagesAdapter.addToStart(messages.get(messages.size() - 1), true);
                                    try {
                                        lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt().getTime();
                                    } catch (Exception e) {

                                    }
                                }
                            });
                            return null;
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 2000);
        } else {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        String response = CloudDataService.getMessages(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), Integer.parseInt(dialogModel.getUsers().get(0).getId()));
                        SharedInstance.setUserMessages(new Gson().fromJson(response, Messages.class));
                        runOnUiThread(() -> {
                            ArrayList<Message> messages = MessagesFixtures.getMessages();
                            if (size[0] > 0 && messages.size() > size[0]) {
                                try {
                                    if (pd.isShowing()) {
                                        pd.dismiss();
                                    }
                                } catch (Exception e) {

                                }
                                size[0] = messages.size();
                                messagesAdapter.addToStart(messages.get(messages.size() - 1), true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 2000);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }



    @Override
    public boolean onSubmit(CharSequence input) {
        Message message = MessagesFixtures.getSenderTextMessage(input.toString());
        super.messagesAdapter.addToStart(message, true);
        JSONObject jsonObject = new JSONObject();
        try {
            size[0] = size[0] + 1;
            if (isRoomChat) {
                jsonObject.put("roomId", dialogModel.getId());
                jsonObject.put("message", message.getText());
                jsonObject.put("createdAt", message.getCreatedAt());
                CloudDataService.sendRoomMessage(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject);
            } else if (DefaultDialogsActivity.isGroupChat) {
                String date = getDate();
                String extension = SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, SharedPrefManager.read(SharedPrefManager.USER_TYPE, ""));
                jsonObject.put("message", message.getText());
//                jsonObject.put("created_at", getDate(new Date().getTime(), "yyyy-MM-dd'T'HH:mm:ssZ"));
                jsonObject.put("created_at", date);
                jsonObject.put("sender_type", extension.toLowerCase());
                CloudDataService.sendGroupMessage(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject);
                try {
                    long currentTime = getConvertedDate(date).getTime();
                    long difference = TimeUnit.MILLISECONDS.toMinutes(currentTime - lastLoadedDate);
                    if (difference >= 30) {
                        sendMembersChatNotification();
                        SharedPrefManager.write(SharedPrefManager.MESSAGE_TIME, currentTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                if (SharedPrefManager.read(SharedPrefManager.MESSAGE_TIME, 0L) == 0) {
//                    SharedPrefManager.write(SharedPrefManager.MESSAGE_TIME, new Date().getTime());
//                    sendMembersChatNotification();
//                } else {
//                    long startTime = SharedPrefManager.read(SharedPrefManager.MESSAGE_TIME, 0L);
//                    long currentTime = getConvertedDate(getDate()).getTime();
//                    long difference = TimeUnit.MILLISECONDS.toMinutes(currentTime - startTime);
//                    if (difference >= 30) {
//                        sendMembersChatNotification();
//                        SharedPrefManager.write(SharedPrefManager.MESSAGE_TIME, currentTime);
//                    }
//                }
            } else {
                jsonObject.put("friend_id", dialogModel.getUsers().get(0).getId());
                jsonObject.put("message", message.getText());
                jsonObject.put("created_at", message.getCreatedAt());
                CloudDataService.sendMessage(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject);
                try {
                    new Thread(() -> sendOneSignalNotification(dialogModel.getUsers().get(0).getId(), SharedPrefManager.read(SharedPrefManager.USER_NAME, ""))).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == 1) {
                Uri selectedFileUri = data.getData();
                picturePath = FilePath.getPath(this, selectedFileUri);
                if (picturePath.isEmpty()) {
                    pickiT.getPath(data.getData(), Build.VERSION.SDK_INT);
                } else {
                    try {
                        if (picturePath.endsWith("jpg") || picturePath.endsWith("jpeg") || picturePath.endsWith("png")) {
                            isFile = false;
                        } else {
                            isFile = true;
                        }

                        simpleResponse.onResponse(selectedFileUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    }

    @Override
    public void onAddAttachments() {
        setSimpleResponse(response -> {
            Message message = null;
            if (picturePath.endsWith("jpg") || picturePath.endsWith("jpeg") || picturePath.endsWith("png")) {
                message = MessagesFixtures.sendImageMessage(response);
                super.messagesAdapter.addToStart(message, true);
                size[0] = size[0] + 1;
            } else if (picturePath.endsWith(".mp4") || picturePath.endsWith(".flv") || picturePath.endsWith(".mkv")){
                pd.setMessage("Uploading Video...");
                pd.setCancelable(false);
                pd.show();
                message = MessagesFixtures.getSenderTextMessage(String.valueOf(response));
//                super.messagesAdapter.addToStart(message, true);
//                size[0] = size[0] + 1;
            } else {
                message = MessagesFixtures.sendFileMessage(response);
//                super.messagesAdapter.addToStart(message, true);
//                size[0] = size[0] + 1;
            }
            if (DefaultDialogsActivity.isGroupChat) {
                String date = getDate();
                String extension = SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, SharedPrefManager.read(SharedPrefManager.USER_TYPE, ""));
                CloudDataService.sendMessageAttachment(ConstantStrings.URLS.Cloud.GROUP_CHAT_FILE, SharedPrefManager.read(SharedPrefManager.TOKEN, ""), picturePath, date, isFile, extension.toLowerCase(), new Function1<String, Unit>() {
                    @Override
                    public Unit invoke(String s) {
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                        isUploaded =  true;
                        try {
                            long currentTime = getConvertedDate(date).getTime();
                            long difference = TimeUnit.MILLISECONDS.toMinutes(currentTime - lastLoadedDate);
                            if (difference >= 30) {
                                sendMembersChatNotification();
                                SharedPrefManager.write(SharedPrefManager.MESSAGE_TIME, currentTime);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
            }
            else if (isRoomChat) {
                CloudDataService.sendMessageAttachmentRoom(ConstantStrings.URLS.Cloud.ROOMS + "/file", SharedPrefManager.read(SharedPrefManager.TOKEN, ""), picturePath, message.getCreatedAt().toString(), isFile, dialogModel.getId(), new Function1<String, Unit>() {
                    @Override
                    public Unit invoke(String s) {
                        isUploaded =  true;
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                        return null;
                    }
                });
                System.out.println("this is room chat");
            }
            else {
                CloudDataService.sendMessageAttachmentFriend(ConstantStrings.URLS.Cloud.USER_CHAT_FILE, SharedPrefManager.read(SharedPrefManager.TOKEN, ""), picturePath, message.getCreatedAt().toString(), dialogModel.getUsers().get(0).getId(), isFile, new Function1<String, Unit>() {
                    @Override
                    public Unit invoke(String s) {
                        isUploaded =  true;
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                        return null;
                    }
                });
                new Thread(() -> sendOneSignalNotification(dialogModel.getUsers().get(0).getId(), SharedPrefManager.read(SharedPrefManager.USER_NAME, ""))).start();
            }
        });
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("*/*");
        photoPickerIntent = Intent.createChooser(photoPickerIntent, "Choose a file");
        startActivityForResult(photoPickerIntent, 1);
//        JSONObject jsonObject = new JSONObject();
//        try {
//            size[0] = size[0] + 1;
//            if (DefaultDialogsActivity.isGroupChat) {
//                jsonObject.put("message", message.getText());
//                jsonObject.put("created_at", message.getCreatedAt());
//                CloudDataService.sendGroupMessage(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject);
//            } else {
//                jsonObject.put("friend_id", dialogModel.getUsers().get(0).getId());
//                jsonObject.put("message", message.getText());
//                jsonObject.put("created_at", message.getCreatedAt());
//                CloudDataService.sendMessage(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void initAdapter() {
        MessageHolders holders = new MessageHolders()
                .registerContentType(
                        CONTENT_TYPE_VOICE,
                        IncomingVoiceMessageViewHolder.class,
                        R.layout.item_custom_incoming_voice_message,
                        OutcomingVoiceMessageViewHolder.class,
                        R.layout.item_custom_outcoming_voice_message,
                        this);
        super.messagesAdapter = new MessagesListAdapter<>(SharedPrefManager.read(SharedPrefManager.USER_ID, ""), holders, super.imageLoader);
        super.messagesAdapter.enableSelectionMode(this);
        super.messagesAdapter.setLoadMoreListener(this);
        super.messagesAdapter.registerViewClickListener(R.id.messageUserAvatar,
                new MessagesListAdapter.OnMessageViewClickListener<Message>() {
                    @Override
                    public void onMessageViewClick(View view, Message message) {
                        frameLayout.setVisibility(View.VISIBLE);
                        goToFragment(new AllUserProfile(message.getUser().getId(), frameLayout), false);
//                        AppUtils.showToast(DefaultMessagesActivity.this,
//                                message.getUser().getName() + " avatar click",
//                                false);
                    }
                });
        super.messagesAdapter.registerViewClickListener(R.id.image,
                new MessagesListAdapter.OnMessageViewClickListener<Message>() {
                    @Override
                    public void onMessageViewClick(View view, Message message) {
                        try {
                            if (message.getImageUrl() != null && (message.getImageUrl().contains("video") || message.getImageUrl().endsWith(".mp4") || message.getImageUrl().endsWith(".flv") || message.getImageUrl().endsWith(".mkv"))) {
                                if (message.getImageUrl().startsWith("content://")) {
                                    if (!isUploaded) {
                                        Toast.makeText(DefaultMessagesActivity.this, mContext.getResources().getString(R.string.VideUploading), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(picturePath));
                                        intent.setDataAndType(Uri.parse(message.getImageUrl()), "video/*");
                                        DefaultMessagesActivity.this.startActivity(intent);
                                    }
                                } else {
                                    new DownloadFileFromURL().execute(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + message.getImageUrl());
                                }
                            } else {
                                FullscreenImageView.open(DefaultMessagesActivity.this, message.getImageUrl(), false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        super.messagesAdapter.registerViewClickListener(R.id.messageText,
                new MessagesListAdapter.OnMessageViewClickListener<Message>() {
                    @Override
                    public void onMessageViewClick(View view, Message message) {
                        try {
                            if (message.getText() != null && (message.getText().contains("video") || message.getText().endsWith(".mp4") || message.getText().endsWith(".flv") || message.getText().endsWith(".mkv"))) {
                                if (message.getText().startsWith("content://")) {
                                    if (!isUploaded) {
                                        Toast.makeText(DefaultMessagesActivity.this, mContext.getResources().getString(R.string.VideUploading), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(picturePath));
                                        intent.setDataAndType(Uri.parse(picturePath), "video/*");
                                        DefaultMessagesActivity.this.startActivity(intent);
                                    }
                                } else {
                                    String url =  message.getText();
                                    if (url.startsWith("Video")) {
                                        url = url.substring(url.lastIndexOf(":") + 1).trim();
                                        new DownloadFileFromURL().execute(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + url);
                                    } else {
                                        new DownloadFileFromURL().execute(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + url);
                                    }
                                }
                            } else if (message.getText().contains("chat/attachments")) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + message.getText()));
                                startActivity(browserIntent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        this.messagesList.setAdapter(super.messagesAdapter);
    }

    @Override
    public void onStartTyping() {
        Log.v("Typing listener", getString(R.string.start_typing_status));
    }

    @Override
    public void onStopTyping() {
        Log.v("Typing listener", getString(R.string.stop_typing_status));
    }

    public SimpleResponse<Uri> getSimpleResponse() {
        return simpleResponse;
    }

    public void setSimpleResponse(SimpleResponse<Uri> simpleResponse) {
        this.simpleResponse = simpleResponse;
    }

    public String getPath(Context context, Uri uri) throws URISyntaxException {
//        if ("content".equalsIgnoreCase(uri.getScheme())) {
//            String[] projection = {"_data"};
//            Cursor cursor = null;
//
//            try {
//                cursor = context.getContentResolver().query(uri, projection, null, null, null);
//                int column_index = cursor.getColumnIndexOrThrow("_data");
//                if (cursor.moveToFirst()) {
//                    return cursor.getString(column_index);
//                }
//            } catch (Exception e) {
//                // Eat it
//            }
//        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath();
//        }

        String path = null;
        String[] projection = { MediaStore.Files.FileColumns.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if(cursor == null){
            path = uri.getPath();
        }
        else{
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);

//        return null;
    }

    @Override
    public boolean hasContentFor(Message message, byte type) {
        switch (type) {
            case CONTENT_TYPE_VOICE:
                return message.getVoice() != null
                        && message.getVoice().getUrl() != null
                        && !message.getVoice().getUrl().isEmpty();
        }
        return false;
    }

    @Override
    public void onRecorded(int count, boolean isCancel) {
        System.out.println("This is voice message button");
        try {
            if (count % 2 == 0) {
                if (myAudioRecorder == null) {
                    myAudioRecorder = new MediaRecorder();
                }
                myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                myAudioRecorder.setOutputFile(outputFile);
                myAudioRecorder.prepare();
                myAudioRecorder.start();
            } else {
                myAudioRecorder.stop();
                myAudioRecorder.reset();
                if (!isCancel) {
                    Message message = MessagesFixtures.sendVoiceMessage(Uri.parse(outputFile), getDuration(new File(outputFile)));
                    super.messagesAdapter.addToStart(message, true);
                    size[0] = size[0] + 1;
                    if (DefaultDialogsActivity.isGroupChat) {
                        String date = getDate();
                        String extension = SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, SharedPrefManager.read(SharedPrefManager.USER_TYPE, ""));
                        CloudDataService.sendVoiceMessageGroup(ConstantStrings.URLS.Cloud.GROUP_CHAT_FILE, SharedPrefManager.read(SharedPrefManager.TOKEN, ""), outputFile, date, true, extension.toLowerCase());
                        try {
                            long currentTime = getConvertedDate(date).getTime();
                            long difference = TimeUnit.MILLISECONDS.toMinutes(currentTime - lastLoadedDate);
                            if (difference >= 30) {
                                sendMembersChatNotification();
                                SharedPrefManager.write(SharedPrefManager.MESSAGE_TIME, currentTime);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (!isRoomChat) {
                        CloudDataService.sendVoiceMessage(ConstantStrings.URLS.Cloud.USER_CHAT_FILE, SharedPrefManager.read(SharedPrefManager.TOKEN, ""), outputFile, message.getCreatedAt().toString(), dialogModel.getUsers().get(0).getId(), true);
                        new Thread(() -> sendOneSignalNotification(dialogModel.getUsers().get(0).getId(), SharedPrefManager.read(SharedPrefManager.USER_NAME, ""))).start();
                    } else {
                        CloudDataService.sendVoiceMessageRoom(ConstantStrings.URLS.Cloud.ROOMS + "/file", SharedPrefManager.read(SharedPrefManager.TOKEN, ""), outputFile, message.getCreatedAt().toString(), true, dialogModel.getId());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            SharedInstance.getGroups().getGroupsData().clear();
            if (myAudioRecorder != null) {
                myAudioRecorder.release();
                myAudioRecorder = null;
            }
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDuration(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return formateMilliSeccond(Long.parseLong(durationStr));
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

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.container, fragment).commitAllowingStateLoss();
    }

    private void sendOneSignalNotification(String userId, String userName) {
        try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic Yzc1M2Y4NTAtZjNkNC00NDExLWE5NTgtNTdjMWIwMmQzN2Nl");
            con.setRequestMethod("POST");

            String strJsonBody = "{"
                    +   "\"app_id\": \"af0bb11e-f674-47a8-8718-bc1c78c36019\","
                    +   "\"android_channel_id\": \"163157c3-fe29-49c0-b9bf-f713dc59eff4\","
                    +   "\"include_external_user_ids\": ["+ userId +"],"
                    +   "\"data\": {\"foo\": \"bar\"},"
                    +   "\"headings\": {\"en\": \"New Message\"},"
                    +   "\"contents\": {\"en\": \"You have a new message from " + userName + "\"}"
                    + "}";


            System.out.println("strJsonBody:\n" + strJsonBody);

            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            System.out.println("httpResponse: " + httpResponse);

            if (  httpResponse >= HttpURLConnection.HTTP_OK
                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            System.out.println("jsonResponse:\n" + jsonResponse);
            con.disconnect();

        } catch(Throwable t) {
            t.printStackTrace();
        }


//        try {
//            JSONObject notificationContent = new JSONObject("{'contents': {'en': 'The notification message or body'}," +
//                    "'include_external_user_ids': ['" + userId + "'], " +
//                    "'headings': {'en': 'Notification Title'}}");
//            OneSignal.postNotification(notificationContent, new OneSignal.PostNotificationResponseHandler() {
//                @Override
//                public void onSuccess(JSONObject response) {
//                    System.out.println("Success");
//                }
//
//                @Override
//                public void onFailure(JSONObject response) {
//                    System.out.println("Failure");
//                }
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void PickiTonUriReturned() {

    }

    @Override
    public void PickiTonStartListener() {

    }

    @Override
    public void PickiTonProgressUpdate(int progress) {

    }

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {
        picturePath = path;
        simpleResponse.onResponse(Uri.parse(path));
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        String pathFolder = "";
        String pathFile = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(DefaultMessagesActivity.this);
            pd.setTitle("Processing...");
            pd.setMessage("Please wait.");
            pd.setMax(100);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setCancelable(true);
            pd.show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                pathFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES + "/VGINV").getAbsolutePath();
                pathFile = new File(pathFolder, System.currentTimeMillis() + ".mp4").getAbsolutePath();
                File futureStudioIconFile = new File(pathFolder);
                if(!futureStudioIconFile.exists()){
                    futureStudioIconFile.mkdirs();
                }
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int lengthOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                FileOutputStream output = new FileOutputStream(pathFile);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return pathFile;
        }

        protected void onProgressUpdate(String... progress) {
            pd.setProgress(Integer.parseInt(progress[0]));
            System.out.println("Progress is: " + Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (pd!=null) {
                pd.dismiss();
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(pathFile));
            intent.setDataAndType(Uri.parse(pathFile), "video/*");
            mContext.startActivity(intent);
        }
    }


    private void sendMembersChatNotification() {
        try {
            new Thread(() -> {
                try {
                    String jsonResponse;

                    URL url = new URL("https://onesignal.com/api/v1/notifications");
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    con.setUseCaches(false);
                    con.setDoOutput(true);
                    con.setDoInput(true);

                    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    con.setRequestProperty("Authorization", "Basic Yzc1M2Y4NTAtZjNkNC00NDExLWE5NTgtNTdjMWIwMmQzN2Nl");
                    con.setRequestMethod("POST");

                    String strJsonBody = "{"
                            +   "\"app_id\": \"af0bb11e-f674-47a8-8718-bc1c78c36019\","
                            +   "\"android_channel_id\": \"163157c3-fe29-49c0-b9bf-f713dc59eff4\","
                            +   "\"included_segments\": [\"All\"],"
                            +   "\"data\": {\"foo\": \"bar\"},"
                            +   "\"headings\": {\"en\": \"Members Chat\"},"
                            +   "\"contents\": {\"en\": \"Members Chat Started..\"}"
                            + "}";


                    System.out.println("strJsonBody:\n" + strJsonBody);

                    byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                    con.setFixedLengthStreamingMode(sendBytes.length);

                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(sendBytes);

                    int httpResponse = con.getResponseCode();
                    System.out.println("httpResponse: " + httpResponse);

                    if (  httpResponse >= HttpURLConnection.HTTP_OK
                            && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                        Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                        scanner.close();
                    }
                    else {
                        Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                        scanner.close();
                    }
                    System.out.println("jsonResponse:\n" + jsonResponse);
                    con.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch(Exception t) {
            t.printStackTrace();
        }
    }

    public static String getDate() {
//        // Create a DateFormatter object for displaying date in specified format.
//        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
//        // Create a calendar object that will convert the date and time value in milliseconds to date.
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(milliSeconds);
//        return formatter.format(calendar.getTime());

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        return nowAsISO;
    }

    public static Date getConvertedDate(String dtStart) {
        try {
            Date date = df.parse(dtStart);
            if (date != null) {
                return date;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
