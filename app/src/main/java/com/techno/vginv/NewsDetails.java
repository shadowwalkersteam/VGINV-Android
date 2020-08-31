package com.techno.vginv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.techno.vginv.Model.News;
import com.techno.vginv.utils.SharedPrefManager;

public class NewsDetails extends AppCompatActivity {

    private TextView heading, description;
    private ImageView imageView;
    private static String url = "";
    private static News news = null;

    public static void open(Context context, News news1) {
        url = news1.getThumbnail();
        news = news1;
        context.startActivity(new Intent(context, NewsDetails.class));
    }

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();

        if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
            if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equalsIgnoreCase("vg")) {
                theme.applyStyle(R.style.AppTheme_NoActionBar, true);
            } else {
                theme.applyStyle(R.style.AppThemeHMG_NoActionBar, true);
            }
        } else {
            if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg")) {
                theme.applyStyle(R.style.AppTheme_NoActionBar, true);
            } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("hmg")) {
                theme.applyStyle(R.style.AppThemeHMG_NoActionBar, true);
            } else {
                theme.applyStyle(R.style.AppTheme_NoActionBar, true);
            }
        }
        // you could also use a switch if you have many themes that could apply
        return theme;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        heading = findViewById(R.id.heading);
        description = findViewById(R.id.description);
        imageView = findViewById(R.id.image);

        Glide.with(getApplicationContext().getApplicationContext())
                .load(url)
                .into(imageView);

        heading.setText(news.getTitle());
        description.setText(news.getDescription());
    }
}
