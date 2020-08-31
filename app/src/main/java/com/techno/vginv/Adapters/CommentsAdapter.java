package com.techno.vginv.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.Fragments.AllUserProfile;
import com.techno.vginv.Model.ProjectComments;
import com.techno.vginv.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter  extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>{
    private Context mContext;
    private List<ProjectComments> friendsList;
    private FrameLayout frameLayout;

    public CommentsAdapter(Context context, List<ProjectComments> newsList, FrameLayout frameLayout) {
        mContext = context;
        friendsList = newsList;
        this.frameLayout = frameLayout;
    }


    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_adapter, parent, false);
        return new CommentsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        try {
            ProjectComments currentNews = friendsList.get(position);
            holder.name.setText(currentNews.getCommentsUsers().getFirstName() + " " + currentNews.getCommentsUsers().getLastName());
            holder.desgination.setText(currentNews.getComment());
            Glide.with(mContext.getApplicationContext())
                    .load(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + "/" + currentNews.getCommentsUsers().getProfilePicture())
                    .into(holder.thumbnailImageView);

            holder.thumbnailImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        frameLayout.setVisibility(View.VISIBLE);
                        goToFragment(new AllUserProfile(currentNews.getId(), frameLayout), true);
                        ((AppCompatActivity) mContext).setTitle(mContext.getResources().getString(R.string.profile));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        frameLayout.setVisibility(View.VISIBLE);
                        goToFragment(new AllUserProfile(currentNews.getId(), frameLayout), true);
                        ((AppCompatActivity) mContext).setTitle(mContext.getResources().getString(R.string.profile));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        private ImageView chat;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contact_name);
            desgination = itemView.findViewById(R.id.designation);
            thumbnailImageView = itemView.findViewById(R.id.profile_image);
            chat = itemView.findViewById(R.id.messageFriend);
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
