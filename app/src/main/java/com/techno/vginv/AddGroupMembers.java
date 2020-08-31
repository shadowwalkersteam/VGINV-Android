package com.techno.vginv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.techno.vginv.Adapters.AddFriendsAdapter;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.Model.Friends;
import com.techno.vginv.Model.GroupInfo;
import com.techno.vginv.Model.GroupMembersToAdd;
import com.techno.vginv.Views.EmptyRecyclerView;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class AddGroupMembers extends AppCompatActivity {
    private Button submit;
    public static ArrayList<Integer> friendsList = new ArrayList<>();
    private ProgressDialog dialog;
    private static String roomID = "";
    private FrameLayout frameLayout;

    public static void open(Context context, String roomId) {
        roomID = roomId;
        context.startActivity(new Intent(context, AddGroupMembers.class));
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
        setContentView(R.layout.activity_add_group_members);
        submit = findViewById(R.id.submit);
        frameLayout = findViewById(R.id.container);
        dialog = new ProgressDialog(this);

        friendsList.clear();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        EmptyRecyclerView mRecyclerView = findViewById(R.id.addFriendsRV);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        List<Friends> friends = new ArrayList<>();

        submit.setOnClickListener(v -> {
            dialog.setMessage(getResources().getString(R.string.pleasewait));
            dialog.show();

            JSONArray arr = new JSONArray();
            for (Integer id : friendsList){
                arr.put(id);
            }

            CloudDataService.addMembers(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), roomID, arr);

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
                    finish();
                    onBackPressed();
                });
            }).start();
        });

        try {
            dialog.setMessage(getResources().getString(R.string.pleasewait));
            dialog.show();
            CloudDataService.getRoomInfo(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), roomID, s -> {
                GroupInfo groupInfo = new Gson().fromJson(s, GroupInfo.class);
                for (GroupMembersToAdd groupMembers : groupInfo.getGroupDetails().getGroupMembersToAdd()) {
                    Friends friends1 = new Friends(groupMembers.getFirstName() + " " + groupMembers.getLastName(), "", ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + groupMembers.getProfilePicture(), groupMembers.getId(), "");
                    friends.add(friends1);
                }
                runOnUiThread(() -> {
                    AddFriendsAdapter mAdapter = new AddFriendsAdapter(this, friends, false, true, frameLayout);
                    mRecyclerView.setAdapter(mAdapter);
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
}
