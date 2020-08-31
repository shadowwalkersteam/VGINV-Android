package com.techno.vginv;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.techno.vginv.Fragments.ChangeFragment;
import com.techno.vginv.Fragments.ProfileFragment;
import com.techno.vginv.features.main.MainActivity;
import com.techno.vginv.utils.SharedPrefManager;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private RelativeLayout changePassword, editProfile, changeLangugae;
    private FrameLayout frameLayout;
    private Toolbar toolbar;
    private TextView textView;
    private ImageView langImage, profileImage, passImage;

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

    private void alignViews(View view) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.LEFT_OF, RelativeLayout.TRUE);
        view.setLayoutParams(params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        changeLangugae = findViewById(R.id.changeLanguage);
        changePassword = findViewById(R.id.changePassLayout);
        editProfile = findViewById(R.id.editProfileLayout);
        frameLayout = findViewById(R.id.container);

        langImage = findViewById(R.id.langImage);
        profileImage = findViewById(R.id.profile_image);
        passImage = findViewById(R.id.passImage);

        toolbar = findViewById(R.id.toolbar);
        textView = toolbar.findViewById(R.id.toolbar_title);

        textView.setText(getResources().getString(R.string.Settings));

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        changePassword.setOnClickListener(v -> {
            frameLayout.setVisibility(View.VISIBLE);
            goToFragment(new ChangeFragment(), false);
        });

        editProfile.setOnClickListener(v -> {
            frameLayout.setVisibility(View.VISIBLE);
            goToFragment(new ProfileFragment(true), false);
        });

        changeLangugae.setOnClickListener(v -> {
            showAlertDialog();
        });

        alignViews(langImage);
        alignViews(profileImage);
        alignViews(passImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        frameLayout.setVisibility(View.INVISIBLE);
    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.replace(R.id.container, fragment).commit();
    }

    @Override public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);
        alertDialog.setTitle(getResources().getString(R.string.Change_Language));
        String[] items = {"English","العربية"};
        int checkedItem = SharedPrefManager.read(SharedPrefManager.LANGUAGE, 0);
        alertDialog.setSingleChoiceItems(items, checkedItem, (dialog, which) -> {
            switch (which) {
                case 0:
                    try {
                        System.out.println("English");
                        setApplicationLanguage("en");
                        SharedPrefManager.write(SharedPrefManager.LANGUAGE, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    try {
                        System.out.println("Arabic");
                        setApplicationLanguage("ar");
                        SharedPrefManager.write(SharedPrefManager.LANGUAGE, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        });
        alertDialog.setNegativeButton(getString(R.string.close),
                (dialog, id) -> {
                    System.out.println("Dialog close");
                });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void setApplicationLanguage(String language) {
        try {
            Locale myLocale = new Locale(language);
            Resources res = SettingsActivity.this.getResources();
            DisplayMetrics display = res.getDisplayMetrics();
            Configuration configuration = res.getConfiguration();
            configuration.locale = myLocale;
            res.updateConfiguration(configuration, display);
            Intent refresh = new Intent(SettingsActivity.this, DashboardActivity.class);
            startActivity(refresh);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
