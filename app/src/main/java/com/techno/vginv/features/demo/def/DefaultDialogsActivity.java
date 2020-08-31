package com.techno.vginv.features.demo.def;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.techno.vginv.DashboardActivity;
import com.techno.vginv.Fragments.AddFriends;
import com.techno.vginv.Fragments.AllUserProfile;
import com.techno.vginv.Fragments.FriendsFragment;
import com.techno.vginv.Model.AllChats;
import com.techno.vginv.Model.Groups;
import com.techno.vginv.R;
import com.techno.vginv.SharedInstance;
import com.techno.vginv.data.fixtures.DialogsFixtures;
import com.techno.vginv.data.model.Dialog;
import com.techno.vginv.data.model.Message;
import com.techno.vginv.features.demo.DemoDialogsActivity;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class DefaultDialogsActivity extends DemoDialogsActivity {

    public static void open(Context context, boolean isGroup) {
        isGroupChat = isGroup;
        context.startActivity(new Intent(context, DefaultDialogsActivity.class));
    }

    private DialogsList dialogsList;
    private ProgressDialog dialog;
    public static boolean isGroupChat = false;
    private Toolbar toolbar;
    private TextView textView, dataDetail;
    private FrameLayout frameLayout;

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
        setContentView(R.layout.activity_default_dialogs);
        dialogsList = (DialogsList) findViewById(R.id.dialogsList);
        toolbar = findViewById(R.id.toolbar);
        textView = toolbar.findViewById(R.id.toolbar_title);
        dataDetail = findViewById(R.id.dataDetail);
        frameLayout = findViewById(R.id.container);

        textView.setText(getResources().getString(R.string.Messages));

        dataDetail.setOnClickListener(v -> {
            frameLayout.setVisibility(View.VISIBLE);
            goToFragment(new FriendsFragment(frameLayout), false);
        });

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        dialog = new ProgressDialog(this);
        dialog.setMessage(getResources().getString(R.string.pleasewait));
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        frameLayout.setVisibility(View.INVISIBLE);
        if (!isGroupChat) {
            try {
                CloudDataService.getAllUsersChats(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), new Function1<String, Unit>() {
                    @Override
                    public Unit invoke(String s) {
                        SharedInstance.setAllChats(new Gson().fromJson(s, AllChats.class));
                        runOnUiThread(() -> {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            initAdapter();
                        });
                        return null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                CloudDataService.getAllGroupChats(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), new Function1<String, Unit>() {
                    @Override
                    public Unit invoke(String s) {
                        SharedInstance.setGroups(new Gson().fromJson(s, Groups.class));
                        runOnUiThread(() -> {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            initAdapter();
                        });
                        return null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDialogClick(Dialog dialog) {
        DefaultMessagesActivity.isRoomChat = false;
        DefaultMessagesActivity.isRoomChat = false;
        DefaultMessagesActivity.open(this, dialog,false);
    }

    private void initAdapter() {
        if (DialogsFixtures.getDialogs().size() <= 0) {
            dataDetail.setVisibility(View.VISIBLE);
            dialogsList.setVisibility(View.INVISIBLE);
        } else {
            super.dialogsAdapter = new DialogsListAdapter<>(super.imageLoader);
            super.dialogsAdapter.setItems(DialogsFixtures.getDialogs());
            super.dialogsAdapter.setOnDialogClickListener(this);
            super.dialogsAdapter.setOnDialogLongClickListener(this);
            dialogsList.setAdapter(super.dialogsAdapter);
            dataDetail.setVisibility(View.INVISIBLE);
            dialogsList.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.INVISIBLE);
        }
    }

    //for example
    public void onNewMessage(String dialogId, Message message) {
        boolean isUpdated = dialogsAdapter.updateDialogWithMessage(dialogId, message);
        if (!isUpdated) {
            //Dialog with this ID doesn't exist, so you can create new Dialog or update all dialogs list
        }
    }

    //for example
    private void onNewDialog(Dialog dialog) {
        dialogsAdapter.addItem(dialog);
    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.container, fragment).commitAllowingStateLoss();
    }
}
