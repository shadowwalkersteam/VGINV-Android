package com.techno.vginv;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;
import com.google.gson.Gson;
import com.joanzapata.iconify.widget.IconButton;
import com.onesignal.OneSignal;
import com.techno.vginv.Adapters.FriendsAdapter;
import com.techno.vginv.Adapters.MenuAdapter;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.Fragments.AddFriends;
import com.techno.vginv.Fragments.CategoriesFragment;
import com.techno.vginv.Fragments.ChangeFragment;
import com.techno.vginv.Fragments.FriendsFragment;
import com.techno.vginv.Fragments.MainFragment;
import com.techno.vginv.Fragments.Notifications;
import com.techno.vginv.Fragments.ProfileFragment;
import com.techno.vginv.Fragments.ProjectsFragment;
import com.techno.vginv.Model.AllChats;
import com.techno.vginv.Model.AllCitites;
import com.techno.vginv.Model.AllCountries;
import com.techno.vginv.Model.AllDepartments;
import com.techno.vginv.Model.AllNotifications;
import com.techno.vginv.Model.AllUsers;
import com.techno.vginv.Model.FriendDetails;
import com.techno.vginv.Model.Friends;
import com.techno.vginv.Model.GetFriends;
import com.techno.vginv.Model.Groups;
import com.techno.vginv.features.demo.def.DefaultDialogsActivity;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;
import com.techno.vginv.utils.TimerService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class DashboardActivity extends AppCompatActivity implements DuoMenuView.OnMenuClickListener{

    private MenuAdapter mMenuAdapter;
    public ViewHolder mViewHolder;
    private Button logout;
    private TextView itemMessagesBadgeTextView, username, designation;
    private CircleImageView profileImage;
    public static int notificationsCount = 0;
    public static final int EXTRA_REVEAL_CENTER_PADDING = 40;
    public static int unreadCounts = 0;
    private Timer timer;

    public static ArrayList<String> mTitles = new ArrayList<>();
    public static ArrayList<String> mTitles2 = new ArrayList<>();



    private void alignViews(View view) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.LEFT_OF, RelativeLayout.TRUE);
        view.setLayoutParams(params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        BigImageViewer.initialize(FrescoImageLoader.with(getApplicationContext()));

//        BigImageViewer.initialize(GlideImageLoader.with(getApplicationContext()));

        ActivityCompat.requestPermissions(DashboardActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);


        logout = findViewById(R.id.duo_view_footer_text);
        username = findViewById(R.id.duo_view_header_text_title);
        profileImage = findViewById(R.id.profile_image);
        designation = findViewById(R.id.duo_view_header_text_sub_title);

        alignViews(logout);
        alignViews(profileImage);

        checkForSession();

        try {
            CloudDataService.getUserProfile(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject -> {
                try {
                    SharedPrefManager.write(SharedPrefManager.USER_TYPE, jsonObject.getString("type"));
                    SharedPrefManager.write(SharedPrefManager.LOGGED_IN_USER_TYPE, jsonObject.getString("type"));
                    OneSignal.setExternalUserId(jsonObject.getString("id"));

                    if (jsonObject != null && jsonObject.has("updated_at")) {
                        String newTime = jsonObject.getString("updated_at");
                        SharedPrefManager.write(SharedPrefManager.SESSION_TIME, newTime);
                    }

                    Thread.sleep(1000);

                    runOnUiThread(() -> {
                        if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
                            if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equalsIgnoreCase("vg")) {
                                mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.shortcutOptions)));
                                mTitles2 = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));
                            } else {
                                mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.shortcutOptionsHMG)));
                                mTitles2 = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptionsHMG)));
                            }
                        } else {
                            if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg")) {
                                mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.shortcutOptions)));
                                mTitles2 = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));
                            } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("hmg")) {
                                mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.shortcutOptionsHMG)));
                                mTitles2 = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptionsHMG)));
                            } else {
                                mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.shortcutOptions)));
                                mTitles2 = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));
                            }
                        }

                        if (SharedPrefManager.read(SharedPrefManager.USER_TYPE,"").equalsIgnoreCase("hmg")) {
                            if (Locale.getDefault().getDisplayLanguage().equals("العربية") || getResources().getConfiguration().locale.getDisplayLanguage().equalsIgnoreCase("Arabic")) {
                                mTitles.set(3, "أعضاء HMG");
                                mTitles.set(10, "أعضاء VG");
                                mTitles2.set(4, "أعضاء HMG");
                            } else {
                                mTitles.set(10, "VG Members");
                                mTitles.set(3, "HMG Members");
                                mTitles2.set(4, "HMG Members");
                            }
                        } else {
                            if (Locale.getDefault().getDisplayLanguage().equals("العربية") || getResources().getConfiguration().locale.getDisplayLanguage().equalsIgnoreCase("Arabic")) {
                                mTitles.set(3, "أعضاء VG");
                                mTitles2.set(4, "أعضاء VG");
                                mTitles.set(10, "أعضاء HMG");
                            } else {
                                mTitles.set(10, "HMG Members");
                                mTitles.set(3, "VG Members");
                                mTitles2.set(4, "VG Members");
                            }
                        }

//                        setTitle(mTitles2.get(0));

                        // Initialize the views
                        mViewHolder = new ViewHolder();

                        // Handle toolbar actions
                        handleToolbar();

                        // Handle menu actions
                        handleMenu();

                        // Handle drawer actions
                        handleDrawer();

                        mMenuAdapter.setViewSelected(0, true);

                        // Show main fragment in container
                        goToFragment(new MainFragment(), false);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
////            final PeriodicWorkRequest periodicWorkRequest
////                    = new PeriodicWorkRequest.Builder(TimerService.class, 5, TimeUnit.SECONDS)
////                    .build();
//
//            final OneTimeWorkRequest workRequest =
//                    new OneTimeWorkRequest.Builder(TimerService.class)
//                            .build();
//
//            WorkManager.getInstance(this).enqueue(workRequest);
//
//            WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.getId())
//                    .observe(this, new Observer<WorkInfo>() {
//                        @Override
//                        public void onChanged(@Nullable WorkInfo workInfo) {
//                            //receiving back the data
//                            if (workInfo != null && workInfo.getState().isFinished()) {
//                                String res = workInfo.getOutputData().getString(TimerService.TASK_DESC);
//                            }
//                        }
//                    });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        logout.setOnClickListener(view -> {
            SharedPrefManager.delete();
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
        });

        try {
            CloudDataService.getAllUsersChats(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), s -> {
                SharedInstance.setAllChats(new Gson().fromJson(s, AllChats.class));
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            CloudDataService.getAllGroupChats(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), s -> {
                try {
                    SharedInstance.setGroups(new Gson().fromJson(s, Groups.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            CloudDataService.getCountries(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), s -> {
                SharedInstance.setAllCountries(new Gson().fromJson(s, AllCountries.class));
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            CloudDataService.getFriends(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), s -> {
                try {
                    SharedInstance.setGetFriends(new Gson().fromJson(s, GetFriends.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            CloudDataService.getAllUnreadMessages(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), s -> {
                try {
                    unreadCounts = Integer.parseInt(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            CloudDataService.getAllDepartment(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), s -> {
                SharedInstance.setAllDepartments(new Gson().fromJson(s, AllDepartments.class));
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            CloudDataService.getAllUsersForRoom(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), s -> {
                SharedInstance.setAllRoomUsers(new Gson().fromJson(s, AllUsers.class));
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            CloudDataService.getCities(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), s -> {
                SharedInstance.setAllCities(new Gson().fromJson(s, AllCitites.class));
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    protected void onResume() {
        super.onResume();
        try {
            CloudDataService.getNotifications(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), s -> {
                SharedInstance.setAllNotifications(new Gson().fromJson(s, AllNotifications.class));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (SharedInstance.getAllNotifications().getAllNotifications() != null && SharedInstance.getAllNotifications().getAllNotifications().size() > 0) {
                            try {
                                notificationsCount = SharedInstance.getAllNotifications().getAllNotifications().size();
//                                itemMessagesBadgeTextView.setVisibility(View.VISIBLE);
//                                itemMessagesBadgeTextView.setText(SharedInstance.getAllNotifications().getAllNotifications().size());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleToolbar() {
        setSupportActionBar(mViewHolder.mToolbar);
        mViewHolder.mToolbar.setTitleTextColor(getResources().getColor(R.color.new_login));
        mViewHolder.toolbarTitle.setText(mTitles2.get(0));
        mViewHolder.mToolbar.setOverflowIcon(getResources().getDrawable(R.drawable.plus_border));

        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                mViewHolder.mDuoDrawerLayout,
                mViewHolder.mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

        username.setText(SharedPrefManager.read(SharedPrefManager.USER_NAME,""));
        designation.setText(SharedPrefManager.read(SharedPrefManager.USER_DESIGNATION,""));
        try {
            Glide.with(this)
                    .load(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + SharedPrefManager.read(SharedPrefManager.USER_PORIFLE_PIC, ""))
                    .into(profileImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mViewHolder.mDuoDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Respond when the drawer's position changes
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Respond when the drawer is opened
                username.setText(SharedPrefManager.read(SharedPrefManager.USER_NAME,""));
                designation.setText(SharedPrefManager.read(SharedPrefManager.USER_DESIGNATION,""));

                try {
                    Glide.with(DashboardActivity.this)
                            .load(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL +  SharedPrefManager.read(SharedPrefManager.USER_PORIFLE_PIC, ""))
                            .into(profileImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Respond when the drawer is closed
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Respond when the drawer motion state changes
            }
        });

    }

    private void handleMenu() {
        mMenuAdapter = new MenuAdapter(mTitles2);

        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
        mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
    }

    @Override
    public void onFooterClicked() {

    }

    @Override
    public void onHeaderClicked() {

    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.replace(R.id.container, fragment).commit();
    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {
        // Set the toolbar title
//        setTitle(mTitles2.get(position));
        mViewHolder.toolbarTitle.setText(mTitles2.get(position));

        // Set the right options selected
        mMenuAdapter.setViewSelected(position, true);

        // Navigate to the right fragment
        switch (position) {
            case 1:
                goToFragment(new CategoriesFragment(), true);
                break;
            case 2:
                goToFragment(new ProjectsFragment(), true);
                break;
            case 3:
                goToFragment(new FriendsFragment(), true);
                break;
            case 4:
                goToFragment(new AddFriends(), true);
                break;
            case 5:
                DefaultDialogsActivity.open(this, true);
                break;
            case 6:
//                goToFragment(new ProfileFragment(), true);
                startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
                break;
            case 7:
                if (SharedPrefManager.read(SharedPrefManager.LOGGED_IN_USER_TYPE, "").equalsIgnoreCase("vg")) {
//                    if (SharedPrefManager.read(SharedPrefManager.SWITCH_POPUP, false)) {
//                        SharedPrefManager.delete();
//                        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
//                        Uri newsUri = Uri.parse("https://vginv.com/questionhmg.html?+971565482966");
//                        SharedPrefManager.write(SharedPrefManager.SWITCH_POPUP, true);
//                        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
//                        startActivity(websiteIntent);
//                    } else {
//                        new AlertDialog.Builder(this)
//                                .setTitle(this.getResources().getString(R.string.switchusertitle))
//                                .setMessage(this.getResources().getString(R.string.switchmsg))
//                                .setPositiveButton(this.getResources().getString(R.string.agree), (dialog, which) -> {
//                                    SharedPrefManager.delete();
//                                    startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
//                                    Uri newsUri = Uri.parse("https://vginv.com/questionhmg.html?+971565482966");
//                                    SharedPrefManager.write(SharedPrefManager.SWITCH_POPUP, true);
//                                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
//                                    startActivity(websiteIntent);
//                                })
//                                .setNegativeButton(this.getResources().getString(R.string.close), null)
//                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .show();
//                    }
                    new AlertDialog.Builder(this)
                            .setTitle(this.getResources().getString(R.string.PleaseContactAdminforHMGaccess))
                            .setPositiveButton(this.getResources().getString(R.string.ClickHere), (dialog, which) -> {
//                                    SharedPrefManager.delete();
//                                    context.startActivity(new Intent(context, LoginActivity.class));
//                                    Uri newsUri = Uri.parse("https://vginv.com/questionhmg.html?+971565482966");
//                                    SharedPrefManager.write(SharedPrefManager.SWITCH_POPUP, true);
//                                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
//                                    context.startActivity(websiteIntent);
                                JSONObject jsonObject = new JSONObject();
                                CloudDataService.switchToHmg(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject, s -> {
                                    runOnUiThread(() -> {
                                        Toast.makeText(this, this.getResources().getString(R.string.HMGAdminwillcontactyoushortly), Toast.LENGTH_SHORT).show();
                                    });
                                    return null;
                                });
                            })
                            .setNegativeButton(this.getResources().getString(R.string.close), null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else {
                    if (SharedPrefManager.read(SharedPrefManager.SWITCH_POPUP, false)) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("hello","1234");
                            CloudDataService.toggleUser(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject, s -> {
                                runOnUiThread(() -> {
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
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                });
                                return null;
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle(this.getResources().getString(R.string.switchusertitle))
                                .setMessage(this.getResources().getString(R.string.switchmsg))
                                .setPositiveButton(this.getResources().getString(R.string.agree), (dialog, which) -> {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("hello","1234");
                                        CloudDataService.toggleUser(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject, s -> {
                                            runOnUiThread(() -> {
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
                                                Intent intent = getIntent();
                                                finish();
                                                startActivity(intent);
                                                overridePendingTransition(0, 0);
                                            });
                                            return null;
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                })
                                .setNegativeButton(this.getResources().getString(R.string.close), null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
                break;
            case 8:
                goToFragment(new ChangeFragment(), true);
                break;

            case 9:
                goToFragment(new ProfileFragment(), true);
                break;
            default:
                goToFragment(new MainFragment(), false);
                break;
        }

        // Close the drawer
        mViewHolder.mDuoDrawerLayout.closeDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem item = menu.findItem(R.id.notifications);
        MenuItemCompat.setActionView(item, R.layout.notification_layout_button);

        RelativeLayout badgeLayout = (RelativeLayout) item.getActionView();
        itemMessagesBadgeTextView = (TextView) badgeLayout.findViewById(R.id.badge_textView);
        itemMessagesBadgeTextView.setVisibility(View.INVISIBLE); // initially hidden

        IconButton iconButtonMessages = (IconButton) badgeLayout.findViewById(R.id.badge_icon_button);
        iconButtonMessages.setTextColor(getResources().getColor(R.color.white));

        iconButtonMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTitle(getResources().getString(R.string.notifications));
                goToFragment(new Notifications(), true);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mailBox:
                setTitle(getResources().getString(R.string.inbox));
                DefaultDialogsActivity.isGroupChat = false;
                DefaultDialogsActivity.open(this, false);
                return true;
            case R.id.notifications:
                setTitle(getResources().getString(R.string.notifications));
                goToFragment(new Notifications(), true);
                return true;
            case R.id.action_settings:
                setTitle(getResources().getString(R.string.Settings));
                startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
//                goToFragment(new ProfileFragment(), true);
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public class ViewHolder {
        private DuoDrawerLayout mDuoDrawerLayout;
        private DuoMenuView mDuoMenuView;
        private Toolbar mToolbar;
        public TextView toolbarTitle;

        ViewHolder() {
            mDuoDrawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbarTitle = mToolbar.findViewById(R.id.toolbar_title);
        }
    }

    private void checkForSession() {
        try {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        CloudDataService.getUserSession(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject -> {
                            try {
                                if (jsonObject != null && jsonObject.has("updated_at")) {
                                    System.out.println("Checking for user session");
                                    String newTime = jsonObject.getString("updated_at");
                                    if (!newTime.equalsIgnoreCase(SharedPrefManager.read(SharedPrefManager.SESSION_TIME, ""))) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(DashboardActivity.this, getResources().getString(R.string.SessionMessage), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        SharedPrefManager.delete();
                                        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));

                                        try {
                                            if (timer != null) {
                                                timer.cancel();
                                                timer = null;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
