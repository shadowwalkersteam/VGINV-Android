package com.techno.vginv.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.techno.vginv.Adapters.NotificationsAdapter;
import com.techno.vginv.DashboardActivity;
import com.techno.vginv.Model.AllNotifications;
import com.techno.vginv.Model.NotificationsModel;
import com.techno.vginv.R;
import com.techno.vginv.SharedInstance;
import com.techno.vginv.Views.EmptyRecyclerView;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * A simple {@link Fragment} subclass.
 */
public class Notifications extends Fragment {
    private NotificationsAdapter mAdapter;
    private Button clearNotifications;
    private ProgressDialog dialog;
    private TextView dataDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        clearNotifications = view.findViewById(R.id.clear);
        dataDetail = view.findViewById(R.id.dataDetail);
        dialog = new ProgressDialog(getActivity());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog.setMessage(getResources().getString(R.string.pleasewait));
        dialog.show();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        EmptyRecyclerView mRecyclerView = view.findViewById(R.id.addFriendsRV);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        List<NotificationsModel> friends = new ArrayList<>();
        DashboardActivity.notificationsCount = 0;
        try {
            CloudDataService.getNotifications(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), new Function1<String, Unit>() {
                @Override
                public Unit invoke(String s) {
                    try {
                        SharedInstance.setAllNotifications(new Gson().fromJson(s, AllNotifications.class));
                        for (com.techno.vginv.Model.Notifications notifications : SharedInstance.getAllNotifications().getAllNotifications()) {
                            if (notifications.getType().contains("addFriend")) {
                                try {
                                    String name = "";
                                    String senderID = "";
                                    JSONObject jsonObject = new JSONObject(notifications.getNotificationData());
                                    if (jsonObject.has("sender_name")) {
                                        name = jsonObject.getString("sender_name");
                                        senderID = jsonObject.getString("sender");
                                    }
                                    NotificationsModel friends1 = new NotificationsModel(getString(R.string.friendrequest), name + " " + getString(R.string.friendRequestDetails), "https://cdn1.imggmi.com/uploads/2019/10/26/cea8e39d81ce0744c98a861c5fab9242-full.jpg", notifications.getType(), senderID, notifications.getId());
                                    friends.add(friends1);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mAdapter = new NotificationsAdapter(getActivity(), friends);
                                            mRecyclerView.setAdapter(mAdapter);

                                            if (dialog.isShowing()) {
                                                dialog.cancel();
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (notifications.getType().endsWith("projectComment")) {
                                try {
                                    String name = "";
                                    String project_id = "";
                                    JSONObject jsonObject = new JSONObject(notifications.getNotificationData());
                                    if (jsonObject.has("sender_name")) {
                                        name = jsonObject.getString("sender_name");
                                        project_id = jsonObject.getString("project_id");
                                    }
                                    NotificationsModel friends1 = new NotificationsModel("Project Comment", name + " Add a comment in a project that you invest.", "", notifications.getType(), project_id, notifications.getId());
                                    friends.add(friends1);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mAdapter = new NotificationsAdapter(getActivity(), friends);
                                            mRecyclerView.setAdapter(mAdapter);

                                            if (dialog.isShowing()) {
                                                dialog.cancel();
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (SharedInstance.getAllNotifications().getAllNotifications().size() <= 0) {
                                    dataDetail.setVisibility(View.VISIBLE);
                                }
                                mAdapter = new NotificationsAdapter(getActivity(), friends);
                                mRecyclerView.setAdapter(mAdapter);

                                if (dialog.isShowing()) {
                                    dialog.cancel();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dataDetail.setVisibility(View.VISIBLE);

                                if (dialog.isShowing()) {
                                    dialog.cancel();
                                }
                            }
                        });
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            dataDetail.setVisibility(View.VISIBLE);
        }
        clearNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Hello world", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
