package com.techno.vginv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.techno.vginv.Adapters.AddFriendsAdapter;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.Model.AllUsersData;
import com.techno.vginv.Model.Friends;
import com.techno.vginv.Model.GroupDetails;
import com.techno.vginv.Model.GroupInfo;
import com.techno.vginv.Model.GroupMembers;
import com.techno.vginv.Model.Messages;
import com.techno.vginv.Model.Rooms;
import com.techno.vginv.Views.EmptyRecyclerView;
import com.techno.vginv.data.model.Dialog;
import com.techno.vginv.features.demo.def.DefaultMessagesActivity;
import com.techno.vginv.features.demo.def.DefaultRoomsActivity;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupSettings extends AppCompatActivity {

    private TextView groupName, groupDescription;
    private Button deleteGroup, updateGroup, addMembers;
    private CircleImageView groupPicture;
    private EmptyRecyclerView mRecyclerView;
    private FrameLayout frameLayout;

    private ProgressDialog dialog;
    private static String roomID = "";

    private String name = "";
    private String description = "";
    private String profilePicture = "";
    private String adminID = "";
    private boolean isAdmin = false;
    public static boolean isGroupDeleted = false;

    public static void open(Context context, String roomId) {
        roomID = roomId;
        context.startActivity(new Intent(context, GroupSettings.class));
    }

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
        setContentView(R.layout.activity_group_settings);
        groupDescription = findViewById(R.id.groupDescription);
        groupName = findViewById(R.id.groupName);
        groupPicture = findViewById(R.id.profile_image);
        deleteGroup = findViewById(R.id.delete);
        updateGroup = findViewById(R.id.settings);
        addMembers = findViewById(R.id.addMembers);
        frameLayout = findViewById(R.id.container);
        dialog = new ProgressDialog(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView = findViewById(R.id.members);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
            if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equalsIgnoreCase("vg")) {
                setBackGroundTint(deleteGroup);
                setBackGroundTint(addMembers);
                setBackGroundTint(updateGroup);
            }
        } else {
            if (!SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg")) {
                setBackGroundTint(deleteGroup);
                setBackGroundTint(addMembers);
                setBackGroundTint(updateGroup);
            }
        }

        deleteGroup.setOnClickListener(v -> {
            dialog.setMessage(getResources().getString(R.string.pleasewait));
            dialog.show();
            CloudDataService.deleteRoom(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), roomID);
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(() -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    isGroupDeleted = true;
                    finish();
                    onBackPressed();
                });
            }).start();

        });

        addMembers.setOnClickListener(v -> {
            AddGroupMembers.open(GroupSettings.this, roomID);
        });

        updateGroup.setOnClickListener(v -> {
            GroupInfoUpdate.open(GroupSettings.this, roomID, name, description);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        frameLayout.setVisibility(View.INVISIBLE);
        try {
            dialog.setMessage(getResources().getString(R.string.pleasewait));
            dialog.show();
        } catch (Exception e) {

        }
        List<Friends> friends = new ArrayList<>();
        try {
            CloudDataService.getRoomInfo(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), roomID, s -> {
                if (s.isEmpty()) {
                    runOnUiThread(() -> {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    });
                    return null;
                }
                GroupInfo groupInfo = new Gson().fromJson(s, GroupInfo.class);
                name = groupInfo.getGroupDetails().getName();
                try {
                    description = groupInfo.getGroupDetails().getDescription();
                } catch (Exception e) {

                }
                adminID = groupInfo.getGroupDetails().getAdmin();
                profilePicture = groupInfo.getGroupDetails().getImage();

                if (SharedPrefManager.read(SharedPrefManager.USER_ID,"").equals(adminID)) {
                    isAdmin = true;
                } else {
                    isAdmin = false;
                }


                for (GroupMembers groupMembers : groupInfo.getGroupDetails().getGroupMembers()) {
                    String profilePic = "";
                    try {
                        profilePic = groupMembers.getProfilePicture();
                    } catch (Exception e) {

                    }
                    Friends friends1 = new Friends(groupMembers.getFirstName() + " " + groupMembers.getLastName(), "", ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + profilePic, groupMembers.getId(), "");
                    friends.add(friends1);
                }
                runOnUiThread(() -> {
                    if (isAdmin) {
                        com.techno.vginv.Adapters.GroupMembers mAdapter = new com.techno.vginv.Adapters.GroupMembers(this, friends, true, roomID, adminID, frameLayout);
                        mRecyclerView.setAdapter(mAdapter);

                        deleteGroup.setVisibility(View.VISIBLE);
                        updateGroup.setVisibility(View.VISIBLE);
                        addMembers.setVisibility(View.VISIBLE);
                    } else {
                        com.techno.vginv.Adapters.GroupMembers mAdapter = new com.techno.vginv.Adapters.GroupMembers(this, friends, false, roomID, adminID, frameLayout);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                    groupName.setText(name);
                    groupDescription.setText(description);
                    Glide.with(GroupSettings.this)
                            .load(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + profilePicture)
                            .into(groupPicture);
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                });
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBackGroundTint(Button button) {
        Drawable buttonDrawable = button.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.red));
        button.setBackground(buttonDrawable);
    }
}
