package com.techno.vginv.features.demo;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.R;
import com.techno.vginv.data.model.Dialog;
import com.techno.vginv.features.demo.def.DefaultDialogsActivity;
import com.techno.vginv.utils.AppUtils;

/*
 * Created by troy379 on 05.04.17.
 */
public abstract class DemoDialogsActivity extends AppCompatActivity
        implements DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog> {

    protected ImageLoader imageLoader;
    protected DialogsListAdapter<Dialog> dialogsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                if (DefaultDialogsActivity.isGroupChat) {
                    imageView.setImageResource(R.drawable.male);
                } else {
                    Picasso.get().load(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + url).into(imageView);
                }
//                imageView.setImageResource(R.drawable.sarah);
//                Glide.with(getApplicationContext().getApplicationContext())
//                        .load(url)
//                        .into(imageView);
            }
        };
    }

    @Override
    public void onDialogLongClick(Dialog dialog) {
        AppUtils.showToast(
                this,
                dialog.getDialogName(),
                false);
    }
}
