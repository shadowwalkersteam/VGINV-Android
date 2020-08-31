package com.techno.vginv.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techno.vginv.CreateGroupActivity;
import com.techno.vginv.DashboardActivity;
import com.techno.vginv.Fragments.AllUserProfile;
import com.techno.vginv.Model.Friends;
import com.techno.vginv.R;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class GroupMembers extends RecyclerView.Adapter<GroupMembers.ViewHolder>{
    private Context mContext;
    private List<Friends> friendsList;
    private boolean isAdmin = false;
    private String roomID = "";
    private String adminID = "";
    private FrameLayout frame;

    public GroupMembers(Context context, List<Friends> newsList, boolean admin, String roomId, String adminId, FrameLayout frameLayout) {
        mContext = context;
        friendsList = newsList;
        isAdmin = admin;
        roomID = roomId;
        adminID = adminId;
        frame = frameLayout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_friends_items, parent, false);
        return new GroupMembers.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Friends currentNews = friendsList.get(position);
        holder.name.setText(currentNews.getName());
        holder.desgination.setText(currentNews.getDesignation());
        holder.addFriends.setImageResource(R.drawable.cross);
        if (currentNews.getProfilePicture().endsWith("male.png")) {
            Glide.with(mContext.getApplicationContext())
                    .load(R.drawable.male)
                    .into(holder.thumbnailImageView);
        } else {
            Glide.with(mContext.getApplicationContext())
                    .load(currentNews.getProfilePicture())
                    .into(holder.thumbnailImageView);
        }

        if (!isAdmin) {
            holder.addFriends.setVisibility(View.GONE);
        }

        if (currentNews.getId().equals(adminID)) {
            holder.desgination.setText(mContext.getResources().getString(R.string.group_admin));
            holder.addFriends.setVisibility(View.GONE);
        }

        holder.addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isAdmin) {
                        CloudDataService.deleteMembers(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), roomID, currentNews.getId());
                        friendsList.remove(position);
                        notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.thumbnailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg") && currentNews.getType().equalsIgnoreCase("hmg")) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.hmgvgerror), Toast.LENGTH_SHORT).show();
                } else {
                    frame.setVisibility(View.VISIBLE);
                    goToFragment(new AllUserProfile(currentNews.getId(), frame), true);
                    ((AppCompatActivity) mContext).setTitle(mContext.getResources().getString(R.string.profile));
                }
            }
        });
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg") && currentNews.getType().equalsIgnoreCase("hmg")) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.hmgvgerror), Toast.LENGTH_SHORT).show();
                } else {
                    frame.setVisibility(View.VISIBLE);
                    goToFragment(new AllUserProfile(currentNews.getId(), frame), true);
                    ((AppCompatActivity) mContext).setTitle(mContext.getResources().getString(R.string.profile));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView desgination;
        private CircleImageView thumbnailImageView;
        private ImageView addFriends;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contact_name);
            desgination = itemView.findViewById(R.id.designation);
            thumbnailImageView = itemView.findViewById(R.id.profile_image);
            addFriends = itemView.findViewById(R.id.add_friend);
        }
    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.container, fragment).commit();
    }
}
