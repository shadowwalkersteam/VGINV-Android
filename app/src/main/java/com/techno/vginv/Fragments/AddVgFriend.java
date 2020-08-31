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
import android.widget.TextView;

import com.google.gson.Gson;
import com.techno.vginv.Adapters.AddFriendsAdapter;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.Model.AllUsers;
import com.techno.vginv.Model.AllUsersData;
import com.techno.vginv.Model.Friends;
import com.techno.vginv.R;
import com.techno.vginv.SharedInstance;
import com.techno.vginv.Views.EmptyRecyclerView;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddVgFriend extends Fragment {

    private AddFriendsAdapter mAdapter;
    private ProgressDialog dialog;
    private TextView dataDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_vg_friend, container, false);
        dataDetail = view.findViewById(R.id.dataDetail);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getResources().getString(R.string.pleasewait));
        dialog.show();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        EmptyRecyclerView mRecyclerView = view.findViewById(R.id.addFriendsRV);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        List<Friends> friends = new ArrayList<>();
        try {
            CloudDataService.getAllUsers(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), "vg", new Function1<String, Unit>() {
                @Override
                public Unit invoke(String s) {
                    try {
                        SharedInstance.setAllUsers(new Gson().fromJson(s, AllUsers.class));
                        for (AllUsersData allUsersData : SharedInstance.getAllUsers().getAllUsers()) {
                            if (!SharedPrefManager.read(SharedPrefManager.USER_ID, "").equals(allUsersData.getId())) {
                                if (SharedPrefManager.read(SharedPrefManager.LOGGED_IN_USER_TYPE, "").equalsIgnoreCase("hmg") &&
                                        allUsersData.getType().equalsIgnoreCase("vg")) {
                                    Friends friends1 = new Friends(allUsersData.getFirstName() + " " + allUsersData.getLastName(), allUsersData.getPosition(), ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + allUsersData.getImage(), allUsersData.getId(), allUsersData.getType());
                                    friends.add(friends1);

                                }
                            }
                        }
                        getActivity().runOnUiThread(() -> {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            if (friends.size() <= 0) {
                                dataDetail.setVisibility(View.VISIBLE);
                            }
                            mAdapter = new AddFriendsAdapter(getActivity(), friends, false, false, null);
                            mRecyclerView.setAdapter(mAdapter);
                        });
                    } catch (Exception e) {
                        getActivity().runOnUiThread(() -> {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            if (friends.size() <= 0) {
                                dataDetail.setVisibility(View.VISIBLE);
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
    }
}
