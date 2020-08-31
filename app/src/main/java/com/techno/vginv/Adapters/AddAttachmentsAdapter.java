package com.techno.vginv.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techno.vginv.AddNewProject;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.FullscreenImageView;
import com.techno.vginv.Model.ProjectAssets;
import com.techno.vginv.R;

import java.io.File;
import java.util.List;

public class AddAttachmentsAdapter extends RecyclerView.Adapter<AddAttachmentsAdapter.ViewHolder> {
    private Context mContext;
    private List<ProjectAssets> assets;

    public AddAttachmentsAdapter(Context context, List<ProjectAssets> newsList) {
        mContext = context;
        assets = newsList;
    }

    @NonNull
    @Override
    public AddAttachmentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_attachments_layout, parent, false);
        return new AddAttachmentsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AddAttachmentsAdapter.ViewHolder holder, int position) {
        final ProjectAssets currentNews = assets.get(position);

        if (currentNews.getId().equals("0")) {
            Glide.with(mContext.getApplicationContext())
                    .load(Uri.parse(currentNews.getPath()))
                    .into(holder.thumbnailImageView);
            holder.title.setText("Attachment" + position++ + ".png");
        } else {
            holder.thumbnailImageView.setVisibility(View.GONE);
            holder.title.setText("Video" + position++);
        }

        holder.download.setOnClickListener(v -> {
            assets.remove(currentNews);
            AddNewProject.projectAssetsArrayList.remove(currentNews);
            notifyDataSetChanged();
        });

        holder.thumbnailImageView.setOnClickListener(v -> {
            if (currentNews.getFilePath().endsWith("jpg") || currentNews.getFilePath().endsWith("jpeg") || currentNews.getFilePath().endsWith("png")) {
                FullscreenImageView.open(mContext, currentNews.getPath(), true);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentNews.getFilePath()));
                intent.setDataAndType(Uri.parse(currentNews.getFilePath()), "video/mp4");
                mContext.startActivity(intent);
            }
        });

        holder.title.setOnClickListener(v -> {
            if (currentNews.getFilePath().endsWith("jpg") || currentNews.getFilePath().endsWith("jpeg") || currentNews.getFilePath().endsWith("png")) {
                FullscreenImageView.open(mContext, currentNews.getPath(), true);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentNews.getFilePath()));
                intent.setDataAndType(Uri.parse(currentNews.getFilePath()), "video/mp4");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return assets.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private CardView parent;
        private ImageView download, thumbnailImageView;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            thumbnailImageView = itemView.findViewById(R.id.thumbnail);
            parent = itemView.findViewById(R.id.card_view);
            download = itemView.findViewById(R.id.download);
        }
    }
}