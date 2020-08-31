package com.techno.vginv.features.demo.def;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.gson.Gson;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.techno.vginv.CreateGroupActivity;
import com.techno.vginv.Model.Rooms;
import com.techno.vginv.R;
import com.techno.vginv.SharedInstance;
import com.techno.vginv.data.fixtures.DialogsFixtures;
import com.techno.vginv.data.model.Dialog;
import com.techno.vginv.features.demo.DemoDialogsActivity;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;

public class DefaultRoomsActivity extends DemoDialogsActivity {

    public static void open(Context context) {
        context.startActivity(new Intent(context, DefaultRoomsActivity.class));
    }

    private DialogsList dialogsList;
    private ProgressDialog dialog;
    private Toolbar toolbar;
    private TextView textView;
    private Button createRoom;

    @Override
    public Resources.Theme getTheme() {
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
        // you could also use a switch if you have many themes that could apply
        return theme;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.Messages);
        setContentView(R.layout.activity_default_rooms);
        dialogsList = findViewById(R.id.dialogsList);
        toolbar = findViewById(R.id.toolbar);
        createRoom = findViewById(R.id.newRoom);
        textView = toolbar.findViewById(R.id.toolbar_title);

        textView.setText(getResources().getString(R.string.Messages));


        if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
            if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equalsIgnoreCase("vg")) {
                setBackGroundTint(createRoom);
            }
        } else {
            if (!SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg")) {
                setBackGroundTint(createRoom);
            }
        }

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        createRoom.setOnClickListener(v -> createNewRoom());

        dialog = new ProgressDialog(this);
        dialog.setMessage(getResources().getString(R.string.pleasewait));
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            SharedInstance.getGroups().getGroupsData().clear();
            CloudDataService.getAllRooms(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), s -> {
                SharedInstance.setRooms(new Gson().fromJson(s, Rooms.class));
                runOnUiThread(() -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    initAdapter();
                });
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDialogClick(Dialog dialog) {
        DefaultDialogsActivity.isGroupChat = false;
        DefaultDialogsActivity.isGroupChat = false;
        DefaultMessagesActivity.open(this, dialog, true);
    }

    private void initAdapter() {
        super.dialogsAdapter = new DialogsListAdapter<>(super.imageLoader);
        super.dialogsAdapter.setItems(DialogsFixtures.getRoomDialogs());

        super.dialogsAdapter.setOnDialogClickListener(this);
        super.dialogsAdapter.setOnDialogLongClickListener(this);

        dialogsList.setAdapter(super.dialogsAdapter);
    }

    private void createNewRoom() {
        startActivity(new Intent(this, CreateGroupActivity.class));
    }

    private void setBackGroundTint(Button button) {
        Drawable buttonDrawable = button.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.red));
        button.setBackground(buttonDrawable);
    }
}
