package com.techno.vginv.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.DashboardActivity;
import com.techno.vginv.Model.ProjectCatalog;
import com.techno.vginv.Model.ProjectDetails;
import com.techno.vginv.R;
import com.techno.vginv.SharedInstance;
import com.techno.vginv.data.model.Dialog;
import com.techno.vginv.data.model.Message;
import com.techno.vginv.data.model.User;
import com.techno.vginv.features.demo.def.DefaultDialogsActivity;
import com.techno.vginv.features.demo.def.DefaultMessagesActivity;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfile extends Fragment {
    private TextView email, phone, address, totalInvestment, totalProjects, favorite, designation, name, description;
    private ProgressDialog dialog;
    private String friendId = "";
    private CircleImageView profile_image;
    private Button sendMessage;
    private LinearLayout projectLayout, dealsLayout;

    private String id, url;

    public UserProfile(String id) {
        friendId = id;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        address = view.findViewById(R.id.address);
        totalInvestment = view.findViewById(R.id.totalInvestment);
        totalProjects = view.findViewById(R.id.totalProjects);
        favorite = view.findViewById(R.id.favorite);
        designation = view.findViewById(R.id.role);
        name = view.findViewById(R.id.name);
        profile_image = view.findViewById(R.id.profile_image);
        dialog = new ProgressDialog(getActivity());
        description = view.findViewById(R.id.description);
        sendMessage = view.findViewById(R.id.sendMessage);
        dealsLayout = view.findViewById(R.id.dealsLayout);
        projectLayout = view.findViewById(R.id.projectLayout);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (friendId.equals(SharedPrefManager.read(SharedPrefManager.USER_ID, ""))) {
            sendMessage.setVisibility(View.GONE);
        }

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<User> users = new ArrayList<>();
                User user = new User(id, name.getText().toString(), url, false);
                users.add(user);
                Dialog dialog = new Dialog(id, name.getText().toString(), url, users, new Message(Long.toString(UUID.randomUUID().getLeastSignificantBits()), user, ""), 0);
                DefaultDialogsActivity.isGroupChat = false;
                DefaultMessagesActivity.open(getContext(), dialog, false);
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:" + phone.getText().toString());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",email.getText().toString(), null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        projectLayout.setOnClickListener(v -> {
            goToFragment(new ProjectDealsFragment(true, true, id), true);
        });

        dealsLayout.setOnClickListener(v -> {

            if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
                if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equalsIgnoreCase("vg")) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage(this.getResources().getString(R.string.no_deals_vg_message))
                            .setPositiveButton(this.getResources().getString(R.string.close), (dialog, which) -> {
                                dialog.cancel();
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    goToFragment(new ProjectDealsFragment(false, true, id), true);
                }
            } else {
                if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg")) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage(this.getResources().getString(R.string.no_deals_vg_message))
                            .setPositiveButton(this.getResources().getString(R.string.close), (dialog, which) -> {
                                dialog.cancel();
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("hmg")) {
                    goToFragment(new ProjectDealsFragment(false, true, id), true);
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setMessage(this.getResources().getString(R.string.no_deals_vg_message))
                            .setPositiveButton(this.getResources().getString(R.string.close), (dialog, which) -> {
                                dialog.cancel();
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });

        dialog.setMessage(getResources().getString(R.string.pleasewait));
        dialog.show();

        CloudDataService.getFriendProfile(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), friendId, new Function1<JSONObject, Unit>() {
            @Override
            public Unit invoke(JSONObject jsonObject) {
                try {
                    if (jsonObject == null) {
                        getActivity().runOnUiThread(() -> {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        });
                        return null;
                    }
                    getActivity().runOnUiThread(() -> {
                        try {
                            if (jsonObject.has("id")) {
                                id = jsonObject.getString("id");
                            }

                            if (jsonObject.has("image")) {
                                url = ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + jsonObject.getString("image");
                            }

                            if (jsonObject.has("first_name") || jsonObject.has("last_name")) {
                                String username = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name");
                                name.setText(username);
                            }
                            if (jsonObject.has("email")) {
                                email.setText(jsonObject.getString("email"));
                            }
                            if (jsonObject.has("phone")) {
                                phone.setText(jsonObject.getString("phone"));
                            }
                            if (jsonObject.has("position")) {
                                designation.setText(jsonObject.getString("position"));
                            }
                            if (jsonObject.has("description")) {
                                description.setText(jsonObject.getString("description"));
                            }

                            Glide.with(getActivity())
                                    .load(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + jsonObject.getString("image"))
                                    .into(profile_image);

                            if (jsonObject.has("City")) {
                                JSONObject city = jsonObject.getJSONObject("City");
                                String cityname = city.getString("city_name");
                                JSONObject jsonObject1 = city.getJSONObject("Country");
                                if (jsonObject1.has("name")) {
                                    String country = jsonObject1.getString("name");
                                    String res = cityname + " / " + country;
                                    address.setText(res);
                                }
                            }
//                            if (jsonObject.has("Invest_Projects")) {
//                                try {
//                                    JSONArray jsonArray = jsonObject.getJSONArray("Invest_Projects");
//                                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
//                                    if (jsonObject1.has("total_invest")) {
//                                        totalInvestment.setText("SAR " + jsonObject1.getString("total_invest"));
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                            }

                            if (jsonObject.has("Department_Users")) {
                                try {
                                    JSONArray jsonArray = jsonObject.getJSONArray("Department_Users");
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                    if (jsonObject1.has("Department")) {
                                        JSONObject jsonObject2 = jsonObject1.getJSONObject("Department");
                                        String res = jsonObject2.getString("dep_en");
                                        if (res.length() > 10) {
                                            favorite.setTextSize(14);
                                            favorite.setText(res);
                                        } else {
                                            favorite.setText(res);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

//                            if (jsonObject.has("completed")) {
//                                totalProjects.setText(jsonObject.getString("completed"));
//                            }

                            int totalProject = 0;
                            int totalDeals = 0;
                            ProjectCatalog projectCatalog1 = new Gson().fromJson(jsonObject.toString(), ProjectCatalog.class);


                            for (ProjectDetails projectCatalog : projectCatalog1.getProjectCatalog()) {
                                if (projectCatalog.getAuth().equals(id)) {
                                    totalProject = totalProject + 1;
                                }
                            }

                            for (ProjectDetails projectCatalog : projectCatalog1.getDealsCatalog()) {
                                if (projectCatalog.getAuth().equals(id)) {
                                    totalDeals = totalDeals + 1;
                                }
                            }

                            totalProjects.setText(String.valueOf(totalProject));
                            totalInvestment.setText(String.valueOf(totalDeals));

                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((DashboardActivity) getContext()).mViewHolder.toolbarTitle.setText(getResources().getString(R.string.profile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.replace(R.id.container, fragment).commit();
    }
}
