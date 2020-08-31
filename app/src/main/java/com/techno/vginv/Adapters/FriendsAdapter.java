package com.techno.vginv.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techno.vginv.DashboardActivity;
import com.techno.vginv.Fragments.ProfileFragment;
import com.techno.vginv.Fragments.UserProfile;
import com.techno.vginv.Model.Friends;
import com.techno.vginv.Model.News;
import com.techno.vginv.R;
import com.techno.vginv.data.fixtures.MessagesFixtures;
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
import java.util.List;
import java.util.Random;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> implements Filterable {
    private Context mContext;
    private List<Friends> friendsList;
    ValueFilter valueFilter;
    List<Friends> mStringFilterList;

    public FriendsAdapter(Context context, List<Friends> newsList) {
        mContext = context;
        friendsList = newsList;
        mStringFilterList = newsList;
    }


    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_items, parent, false);
        return new FriendsAdapter.ViewHolder(v);
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, int position) {
        try {
            Friends currentNews = friendsList.get(position);
            holder.name.setText(currentNews.getName());
            holder.desgination.setText(currentNews.getDesignation());
            Glide.with(mContext.getApplicationContext())
                    .load(currentNews.getProfilePicture())
                    .into(holder.thumbnailImageView);

            if (SharedPrefManager.read(SharedPrefManager.LOGGED_IN_USER_TYPE, "").equalsIgnoreCase("hmg") &&
                    currentNews.getType().equalsIgnoreCase("vg")) {
                holder.vgText.setVisibility(View.VISIBLE);
            }

            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg") && currentNews.getType().equalsIgnoreCase("hmg")) {
//                        Toast.makeText(mContext, mContext.getResources().getString(R.string.hmgvgchaterror), Toast.LENGTH_SHORT).show();
//                    } else {
//
//                    }
                    ArrayList<User> users = new ArrayList<>();
                    User user = new User(currentNews.getId(), currentNews.getName(), currentNews.getProfilePicture(), false);
                    users.add(user);
                    Dialog dialog = new Dialog(currentNews.getId(), currentNews.getName(), currentNews.getProfilePicture(), users, new Message(Long.toString(UUID.randomUUID().getLeastSignificantBits()), user, ""), 0);
                    DefaultDialogsActivity.isGroupChat = false;
                    DefaultMessagesActivity.open(mContext, dialog, false);
                }
            });

            holder.cancel_friend.setOnClickListener(view -> {
                try {
                    new AlertDialog.Builder(mContext)
                            .setTitle(mContext.getResources().getString(R.string.UnFriend))
                            .setMessage(mContext.getResources().getString(R.string.UnFriendMessage))
                            .setPositiveButton(mContext.getResources().getString(R.string.Ok), (dialog, which) -> {
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("friendId", currentNews.getId());
                                    CloudDataService.unFriendUser(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject, s -> {
                                        if (s.contains("You are no longer friends with this member")) {
                                            friendsList.remove(currentNews);
                                            ((DashboardActivity)mContext).runOnUiThread(() -> {
                                                notifyDataSetChanged();
                                            });
                                        }
                                        return null;
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            })
                            .setNegativeButton(mContext.getResources().getString(R.string.close), null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            holder.thumbnailImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg") && currentNews.getType().equalsIgnoreCase("hmg")) {
//                        Toast.makeText(mContext, mContext.getResources().getString(R.string.hmgvgerror), Toast.LENGTH_SHORT).show();
//                    } else {
//                        goToFragment(new UserProfile(currentNews.getId()), true);
//                        ((AppCompatActivity) mContext).setTitle(mContext.getResources().getString(R.string.profile));
//                    }
                    goToFragment(new UserProfile(currentNews.getId()), true);
                    ((AppCompatActivity) mContext).setTitle(mContext.getResources().getString(R.string.profile));
                }
            });
            holder.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg") && currentNews.getType().equalsIgnoreCase("hmg")) {
//                        Toast.makeText(mContext, mContext.getResources().getString(R.string.hmgvgerror), Toast.LENGTH_SHORT).show();
//                    } else {
//                        goToFragment(new UserProfile(currentNews.getId()), true);
//                        ((AppCompatActivity) mContext).setTitle(mContext.getResources().getString(R.string.profile));
//                    }
                    goToFragment(new UserProfile(currentNews.getId()), true);
                    ((AppCompatActivity) mContext).setTitle(mContext.getResources().getString(R.string.profile));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView desgination;
        private CircleImageView thumbnailImageView;
        private ImageView chat, cancel_friend;
        private TextView vgText;

        ViewHolder(View itemView) {
            super(itemView);
            vgText = itemView.findViewById(R.id.vgText);
            name = itemView.findViewById(R.id.contact_name);
            desgination = itemView.findViewById(R.id.designation);
            thumbnailImageView = itemView.findViewById(R.id.profile_image);
            chat = itemView.findViewById(R.id.messageFriend);
            cancel_friend = itemView.findViewById(R.id.cancel_friend);
        }
    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.container, fragment).commit();
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<Friends> filterList = new ArrayList<>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(mStringFilterList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            friendsList = (List<Friends>) results.values;
            notifyDataSetChanged();
        }
    }
}
