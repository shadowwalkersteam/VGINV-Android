package com.techno.vginv.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.techno.vginv.Adapters.FriendsAdapter;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.DashboardActivity;
import com.techno.vginv.Model.FriendDetails;
import com.techno.vginv.Model.Friends;
import com.techno.vginv.Model.GetFriends;
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
public class FriendsFragment extends Fragment {
    private FriendsAdapter mAdapter;
    private ProgressDialog dialog;
    private TextView dataDetail;
    private FrameLayout frameLayout;
    private SearchView searchView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        dataDetail = view.findViewById(R.id.dataDetail);
        searchView = view.findViewById(R.id.searchView);
        dataDetail.setOnClickListener(v -> {
            goToFragment(new AddFriends(), true);
        });
        return view;
    }

    public FriendsFragment() {

    }

    public FriendsFragment(FrameLayout frameLayout) {
        this.frameLayout = frameLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getResources().getString(R.string.pleasewait));
        dialog.show();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        EmptyRecyclerView mRecyclerView = view.findViewById(R.id.friendsRV);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        List<Friends> friends = new ArrayList<>();
        try {
            CloudDataService.getFriends(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), new Function1<String, Unit>() {
                @Override
                public Unit invoke(String s) {
                    try {
                        SharedInstance.getGetFriends().getFriendObject().clear();
                        SharedInstance.setGetFriends(new Gson().fromJson(s, GetFriends.class));
                        for (FriendDetails friendObject : SharedInstance.getGetFriends().getFriendObject()) {
                            if (!SharedPrefManager.read(SharedPrefManager.USER_ID, "").equals(friendObject.getId())) {
                                Friends friends1 = new Friends(friendObject.getFirstName() + " " + friendObject.getLastName(), friendObject.getPosition(), ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + friendObject.getImage(), friendObject.getId(), friendObject.getType());
                                friends.add(friends1);
                            }
                        }
                        getActivity().runOnUiThread(() -> {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            if (friends.size() <= 0) {
                                dataDetail.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.INVISIBLE);
                            } else {
                                mAdapter = new FriendsAdapter(getActivity(), friends);
                                mRecyclerView.setAdapter(mAdapter);
                            }
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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        try {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

            if (addToBackStack) {
                transaction.addToBackStack(null);
            }

            transaction.replace(R.id.container, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        try {
//            frameLayout.setVisibility(View.INVISIBLE);
//        } catch (Exception e) {
//
//        }
//    }
}
