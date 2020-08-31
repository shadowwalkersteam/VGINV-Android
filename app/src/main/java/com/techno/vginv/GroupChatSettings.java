package com.techno.vginv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.Model.Friends;
import com.techno.vginv.Model.GroupChats;
import com.techno.vginv.Model.GroupInfo;
import com.techno.vginv.Model.GroupMembers;
import com.techno.vginv.Views.EmptyRecyclerView;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatSettings extends AppCompatActivity {

    private TextView groupName;
    private EmptyRecyclerView mRecyclerView;
    private FrameLayout frameLayout;
    private ProgressDialog dialog;
    private static String roomID = "";
    private String name = "";
    private String adminID = "";
    private boolean isAdmin = false;
    private Toolbar toolbar;
    private TextView textView;

    public static void open(Context context, String roomId) {
        roomID = roomId;
        context.startActivity(new Intent(context, GroupChatSettings.class));
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
        setContentView(R.layout.activity_group_chat_settings);
        groupName = findViewById(R.id.groupName);
        frameLayout = findViewById(R.id.container);
        toolbar = findViewById(R.id.toolbar);
        textView = toolbar.findViewById(R.id.toolbar_title);

        textView.setText(getResources().getString(R.string.add_member));

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        dialog = new ProgressDialog(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView = findViewById(R.id.members);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
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
        List<Friends> friendsNew = new ArrayList<>();
        try {
            for (GroupChats groupMembers : SharedInstance.getGroups().getGroupsData()) {
                Friends friends1 = new Friends(groupMembers.getSenderDetails().getFirstName() + " " + groupMembers.getSenderDetails().getLastName(), "", ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + groupMembers.getSenderDetails().getProfilePicture(), groupMembers.getId(), "");

//                if (friends.size() > 0) {
//                    for (Friends id : friends) {
//                        if (!id.getId().contains(friends1.getId())) {
//                            friends.add(friends1);
//                        }
//                    }
//                } else {
//
//                }
                friends.add(friends1);
            }

            if (friendsNew.size() <= 0) {
                friendsNew.add(friends.get(0));
                Thread.sleep(3000);
            }


            for (Friends friends1 : friends) {
                Iterator<Friends> itr = friendsNew.iterator();
                while(itr.hasNext()){
                    Friends name = itr.next();
                    if(name.getName().equals(friends1.getName())){
                        friendsNew.add(friends1);
                    }
                }
            }

            com.techno.vginv.Adapters.GroupMembers mAdapter = new com.techno.vginv.Adapters.GroupMembers(this, friendsNew, false, roomID, adminID, frameLayout);
            mRecyclerView.setAdapter(mAdapter);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
