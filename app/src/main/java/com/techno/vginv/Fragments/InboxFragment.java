package com.techno.vginv.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techno.vginv.Adapters.FriendsAdapter;
import com.techno.vginv.Adapters.FriendsChatList;
import com.techno.vginv.Adapters.NotificationsAdapter;
import com.techno.vginv.Model.Friends;
import com.techno.vginv.Model.Inbox;
import com.techno.vginv.R;
import com.techno.vginv.Views.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;


public class InboxFragment extends Fragment {
    private FriendsChatList mAdapter;
    private TextView dataDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        dataDetail = view.findViewById(R.id.dataDetail);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        EmptyRecyclerView mRecyclerView = view.findViewById(R.id.friendListInbox);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        Inbox friends1 = new Inbox("Sarah", "We have a meeting at 3PM today. See you there", "https://content-static.upwork.com/uploads/2014/10/01073427/profilephoto1.jpg");
        List<Inbox> friends = new ArrayList<>();
        friends.add(friends1);
        mAdapter = new FriendsChatList(getActivity(), friends);
        mRecyclerView.setAdapter(mAdapter);
    }
}
