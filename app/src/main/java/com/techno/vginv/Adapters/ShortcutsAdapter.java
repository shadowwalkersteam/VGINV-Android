package com.techno.vginv.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.techno.vginv.DashboardActivity;
import com.techno.vginv.Fragments.AddFriends;
import com.techno.vginv.Fragments.AddVgFriend;
import com.techno.vginv.Fragments.CategoriesFragment;
import com.techno.vginv.Fragments.FriendsFragment;
import com.techno.vginv.Fragments.Notifications;
import com.techno.vginv.Fragments.ProfileFragment;
import com.techno.vginv.Fragments.ProjectsFragment;
import com.techno.vginv.LoginActivity;
import com.techno.vginv.R;
import com.techno.vginv.SettingsActivity;
import com.techno.vginv.features.demo.def.DefaultDialogsActivity;
import com.techno.vginv.features.demo.def.DefaultRoomsActivity;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShortcutsAdapter extends RecyclerView.Adapter<ShortcutsAdapter.ViewHolder> {
    private Context context;
    private List<String> list;

    public ShortcutsAdapter(Context context, List<String> list) {
        this.context = context;
        if (SharedPrefManager.read(SharedPrefManager.USER_TYPE,"").equalsIgnoreCase("hmg")) {
            if (Locale.getDefault().getDisplayLanguage().equals("العربية") || context.getResources().getConfiguration().locale.getDisplayLanguage().equalsIgnoreCase("Arabic")) {
                list.set(3, "أعضاء HMG");
                list.set(10, "أعضاء VG");
            } else {
                list.set(3, "HMG Members");
                list.set(10, "VG Members");
            }
        } else {
            if (Locale.getDefault().getDisplayLanguage().equals("العربية") || context.getResources().getConfiguration().locale.getDisplayLanguage().equalsIgnoreCase("Arabic")) {
                list.set(3, "أعضاء VG");
                list.set(10, "أعضاء HMG");
            } else {
                list.set(3, "VG Members");
                list.set(10, "HMG Members");

            }
        }
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.shortcut_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String test = list.get(position);
        holder.textView.setText(test);

        holder.carview.setOnClickListener(view -> {
            ((DashboardActivity) context).mViewHolder.toolbarTitle.setText(DashboardActivity.mTitles.get(position));
            switch (position) {
                case 0:
                    goToFragment(new Notifications(), true);
                    ((AppCompatActivity) context).setTitle(DashboardActivity.mTitles.get(position));
                    ((DashboardActivity) context).mViewHolder.toolbarTitle.setText(DashboardActivity.mTitles.get(position));
                    break;
                case 1:
                    goToFragment(new CategoriesFragment(), true);
                    ((AppCompatActivity) context).setTitle(DashboardActivity.mTitles.get(position));
                    break;
                case 2:
                    goToFragment(new FriendsFragment(), true);
                    ((AppCompatActivity) context).setTitle(DashboardActivity.mTitles.get(position));
                    break;
                case 3:
                    goToFragment(new AddFriends(), true);
                    ((AppCompatActivity) context).setTitle(DashboardActivity.mTitles.get(position));
                    break;
                case 4:
                    DefaultDialogsActivity.open(context, true);
                    break;
                case 5:
                    context.startActivity(new Intent(context, SettingsActivity.class));
//                    goToFragment(new ProfileFragment(), true);
                    ((AppCompatActivity) context).setTitle(DashboardActivity.mTitles.get(position));
                    break;
                case 6:
                    if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equalsIgnoreCase("vg")) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("hello","1234");
                            CloudDataService.toggleUser(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject, s -> {
                                ((DashboardActivity)context).runOnUiThread(() -> {
                                    if (s.contains("switched to hmg")) {
                                        SharedPrefManager.write(SharedPrefManager.TOGGLER_USER_TYPE, "HMG");
                                    } else {
                                        SharedPrefManager.write(SharedPrefManager.TOGGLER_USER_TYPE, "VG");
                                    }
                                    SharedPrefManager.write(SharedPrefManager.SWITCH_POPUP, true);
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = ((DashboardActivity)context).getIntent();
                                    ((DashboardActivity)context).finish();
                                    ((DashboardActivity)context).startActivity(intent);
                                    ((DashboardActivity)context).overridePendingTransition(0, 0);
                                });
                                return null;
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else if (SharedPrefManager.read(SharedPrefManager.LOGGED_IN_USER_TYPE, "").equalsIgnoreCase("vg")) {
//                        if (SharedPrefManager.read(SharedPrefManager.SWITCH_POPUP, false)) {
//                            SharedPrefManager.delete();
//                            context.startActivity(new Intent(context, LoginActivity.class));
//                            Uri newsUri = Uri.parse("https://vginv.com/questionhmg.html?+971565482966");
//                            SharedPrefManager.write(SharedPrefManager.SWITCH_POPUP, true);
//                            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
//                            context.startActivity(websiteIntent);
//                        } else {
//                            new AlertDialog.Builder(context)
//                                    .setTitle(context.getResources().getString(R.string.switchusertitle))
//                                    .setMessage(context.getResources().getString(R.string.switchmsg))
//                                    .setPositiveButton(context.getResources().getString(R.string.agree), (dialog, which) -> {
//                                        SharedPrefManager.delete();
//                                        context.startActivity(new Intent(context, LoginActivity.class));
//                                        Uri newsUri = Uri.parse("https://vginv.com/questionhmg.html?+971565482966");
//                                        SharedPrefManager.write(SharedPrefManager.SWITCH_POPUP, true);
//                                        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
//                                        context.startActivity(websiteIntent);
//                                    })
//                                    .setNegativeButton(context.getResources().getString(R.string.close), null)
//                                    .setIcon(android.R.drawable.ic_dialog_alert)
//                                    .show();
//                        }
                        new AlertDialog.Builder(context)
                                .setTitle(context.getResources().getString(R.string.PleaseContactAdminforHMGaccess))
                                .setPositiveButton(context.getResources().getString(R.string.ClickHere), (dialog, which) -> {
//                                    SharedPrefManager.delete();
//                                    context.startActivity(new Intent(context, LoginActivity.class));
//                                    Uri newsUri = Uri.parse("https://vginv.com/questionhmg.html?+971565482966");
//                                    SharedPrefManager.write(SharedPrefManager.SWITCH_POPUP, true);
//                                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
//                                    context.startActivity(websiteIntent);
                                    JSONObject jsonObject = new JSONObject();
                                    CloudDataService.switchToHmg(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject, s -> {
                                        ((DashboardActivity)context).runOnUiThread(() -> {
                                            Toast.makeText(context, context.getResources().getString(R.string.HMGAdminwillcontactyoushortly), Toast.LENGTH_SHORT).show();
                                        });
                                        return null;
                                    });
                                })
                                .setNegativeButton(context.getResources().getString(R.string.close), null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                    else {
                        if (SharedPrefManager.read(SharedPrefManager.SWITCH_POPUP, false)) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("hello","1234");
                                CloudDataService.toggleUser(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject, s -> {
                                    ((DashboardActivity)context).runOnUiThread(() -> {
                                        if (s.contains("switched to hmg")) {
                                            SharedPrefManager.write(SharedPrefManager.TOGGLER_USER_TYPE, "HMG");
                                        } else {
                                            SharedPrefManager.write(SharedPrefManager.TOGGLER_USER_TYPE, "VG");
                                        }
                                        SharedPrefManager.write(SharedPrefManager.SWITCH_POPUP, true);
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Intent intent = ((DashboardActivity)context).getIntent();
                                        ((DashboardActivity)context).finish();
                                        ((DashboardActivity)context).startActivity(intent);
                                        ((DashboardActivity)context).overridePendingTransition(0, 0);
                                    });
                                    return null;
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            new AlertDialog.Builder(context)
                                    .setTitle(context.getResources().getString(R.string.switchusertitle))
                                    .setMessage(context.getResources().getString(R.string.switchmsg))
                                    .setPositiveButton(context.getResources().getString(R.string.agree), (dialog, which) -> {
                                        try {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("hello","1234");
                                            CloudDataService.toggleUser(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject, s -> {
                                                ((DashboardActivity)context).runOnUiThread(() -> {
                                                    if (s.contains("switched to hmg")) {
                                                        SharedPrefManager.write(SharedPrefManager.TOGGLER_USER_TYPE, "HMG");
                                                    } else {
                                                        SharedPrefManager.write(SharedPrefManager.TOGGLER_USER_TYPE, "VG");
                                                    }
                                                    SharedPrefManager.write(SharedPrefManager.SWITCH_POPUP, true);
                                                    try {
                                                        Thread.sleep(2000);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    Intent intent = ((DashboardActivity)context).getIntent();
                                                    ((DashboardActivity)context).finish();
                                                    ((DashboardActivity)context).startActivity(intent);
                                                    ((DashboardActivity)context).overridePendingTransition(0, 0);
                                                });
                                                return null;
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    })
                                    .setNegativeButton(context.getResources().getString(R.string.close), null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    }
                    break;

                case 7:
                    DefaultDialogsActivity.isGroupChat = false;
                    DefaultDialogsActivity.open(context, false);
                    ((AppCompatActivity) context).setTitle(DashboardActivity.mTitles.get(position));
                    break;

                case 8:
                    goToFragment(new ProjectsFragment(), true);
                    ((AppCompatActivity) context).setTitle(DashboardActivity.mTitles.get(position));
                    break;

                case 9:
                    DefaultRoomsActivity.open(context);
                    ((AppCompatActivity) context).setTitle(DashboardActivity.mTitles.get(position));
                    break;
                case 10:
//                    if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
//                        if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equalsIgnoreCase("vg")) {
//                            new AlertDialog.Builder(context)
//                                    .setTitle(context.getResources().getString(R.string.members_notification))
//                                    .setMessage(context.getResources().getString(R.string.vg_members_message))
//                                    .setNegativeButton(context.getResources().getString(R.string.close), null)
//                                    .setIcon(android.R.drawable.ic_dialog_alert)
//                                    .show();
//                        } else {
//                            new AlertDialog.Builder(context)
//                                    .setTitle(context.getResources().getString(R.string.members_notification))
//                                    .setMessage(context.getResources().getString(R.string.hmg_members_message))
//                                    .setNegativeButton(context.getResources().getString(R.string.close), null)
//                                    .setIcon(android.R.drawable.ic_dialog_alert)
//                                    .show();
//                        }
//                    } else {
//                        if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg")) {
//                            new AlertDialog.Builder(context)
//                                    .setTitle(context.getResources().getString(R.string.members_notification))
//                                    .setMessage(context.getResources().getString(R.string.vg_members_message))
//                                    .setNegativeButton(context.getResources().getString(R.string.close), null)
//                                    .setIcon(android.R.drawable.ic_dialog_alert)
//                                    .show();
//                        } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("hmg")) {
//                            new AlertDialog.Builder(context)
//                                    .setTitle(context.getResources().getString(R.string.members_notification))
//                                    .setMessage(context.getResources().getString(R.string.hmg_members_message))
//                                    .setNegativeButton(context.getResources().getString(R.string.close), null)
//                                    .setIcon(android.R.drawable.ic_dialog_alert)
//                                    .show();
//                        } else {
//                            new AlertDialog.Builder(context)
//                                    .setTitle(context.getResources().getString(R.string.members_notification))
//                                    .setMessage(context.getResources().getString(R.string.vg_members_message))
//                                    .setNegativeButton(context.getResources().getString(R.string.close), null)
//                                    .setIcon(android.R.drawable.ic_dialog_alert)
//                                    .show();
//                        }
//                    }
                    if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg")) {
                        new AlertDialog.Builder(context)
                                .setTitle(context.getResources().getString(R.string.members_notification))
                                .setMessage(context.getResources().getString(R.string.hmg_members_message))
                                .setNegativeButton(context.getResources().getString(R.string.close), null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("hmg")) {
                        goToFragment(new AddVgFriend(), true);
                        ((AppCompatActivity) context).setTitle(DashboardActivity.mTitles.get(position));
//                        new AlertDialog.Builder(context)
//                                .setTitle(context.getResources().getString(R.string.members_notification))
//                                .setMessage(context.getResources().getString(R.string.vg_members_message))
//                                .setNegativeButton(context.getResources().getString(R.string.close), null)
//                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .show();
                    } else {
                        new AlertDialog.Builder(context)
                                .setTitle(context.getResources().getString(R.string.members_notification))
                                .setMessage(context.getResources().getString(R.string.vg_members_message))
                                .setNegativeButton(context.getResources().getString(R.string.close), null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                    break;
                case 11:
                    goToFragment(new ProfileFragment(), true);
                    ((AppCompatActivity) context).setTitle(DashboardActivity.mTitles.get(position));
                    break;
            }
        });

        switch (position) {
            case 0:
                if (DashboardActivity.notificationsCount > 0) {
                    holder.badgeIcon.setVisibility(View.VISIBLE);
                    holder.badgeIcon.setText(String.valueOf(DashboardActivity.notificationsCount));
                } else {
                    holder.badgeIcon.setVisibility(View.INVISIBLE);
                }
                holder.imageView.setImageResource(R.drawable.notifications3);
                break;
            case 1:
                holder.imageView.setImageResource(R.drawable.categories3);
                break;
            case 2:
                holder.imageView.setImageResource(R.drawable.members3);
                break;
            case 10:
            case 3:
                holder.imageView.setImageResource(R.drawable.add_members3);
                break;
            case 4:
                holder.imageView.setImageResource(R.drawable.group_chat3);
                break;
            case 9:
                holder.imageView.setImageResource(R.drawable.rooms);
                break;
            case 5:
                holder.imageView.setImageResource(R.drawable.settings3);
                break;
            case 6:
                holder.imageView.setImageResource(R.drawable.switch3);
                break;
            case 7:
                if (DashboardActivity.unreadCounts > 0) {
                    holder.badgeIcon.setVisibility(View.VISIBLE);
                    holder.badgeIcon.setText(String.valueOf(DashboardActivity.unreadCounts));
                } else {
                    holder.badgeIcon.setVisibility(View.INVISIBLE);
                }
                holder.imageView.setImageResource(R.drawable.messages3);
                break;
            case 8:
                holder.imageView.setImageResource(R.drawable.deal3);
                break;
            case 11:
                holder.imageView.setImageResource(R.drawable.profile_new_icon);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView, badgeIcon;
        private CircleImageView imageView;
        private ConstraintLayout constraintLayout;
        private CardView carview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.shortcutName);
            imageView = itemView.findViewById(R.id.shortcutImage);
            constraintLayout = itemView.findViewById(R.id.constraint);
            badgeIcon = itemView.findViewById(R.id.badges);
            carview = itemView.findViewById(R.id.carview);
        }
    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.container, fragment).commit();
    }
}
