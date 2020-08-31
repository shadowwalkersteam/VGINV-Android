package com.techno.vginv.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techno.vginv.Model.Inbox;
import com.techno.vginv.R;
import com.techno.vginv.features.demo.def.DefaultDialogsActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsChatList extends RecyclerView.Adapter<FriendsChatList.ViewHolder> {
    private Context mContext;
    private List<Inbox> inboxList;

    public FriendsChatList(Context context, List<Inbox> newsList) {
        mContext = context;
        inboxList = newsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_items, parent, false);
        return new FriendsChatList.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Inbox currentNews = inboxList.get(position);
        holder.name.setText(currentNews.getName());
        holder.desgination.setText(currentNews.getMessage());
        holder.chat.setVisibility(View.INVISIBLE);
        Glide.with(mContext.getApplicationContext())
                .load(currentNews.getProfilePicture())
                .into(holder.thumbnailImageView);
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DefaultDialogsActivity.isGroupChat = false;
                DefaultDialogsActivity.open(mContext, false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return inboxList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView desgination;
        private CircleImageView thumbnailImageView;
        private ImageView chat;
        private CardView card_view;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contact_name);
            desgination = itemView.findViewById(R.id.designation);
            thumbnailImageView = itemView.findViewById(R.id.profile_image);
            chat = itemView.findViewById(R.id.messageFriend);
            card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
