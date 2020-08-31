package com.techno.vginv.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.DashboardActivity;
import com.techno.vginv.Model.News;
import com.techno.vginv.Model.NotificationsModel;
import com.techno.vginv.Model.ProjectCatalog;
import com.techno.vginv.Model.ProjectDetails;
import com.techno.vginv.R;
import com.techno.vginv.SharedInstance;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private Context mContext;
    private List<NotificationsModel> notifications;

    public NotificationsAdapter(Context context, List<NotificationsModel> newsList) {
        mContext = context;
        notifications = newsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_items, parent, false);
        return new NotificationsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NotificationsModel currentNews = notifications.get(position);
        holder.title.setText(currentNews.getTitle());
        holder.description.setText(currentNews.getDescription());
        Glide.with(mContext.getApplicationContext())
                .load(currentNews.getThumbnail())
                .into(holder.thumbnailImageView);
        if (notifications.get(position).getNotificationType().contains("addFriend")) {
            holder.notificationlayout.setVisibility(View.VISIBLE);
            holder.addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String data = currentNews.getNotificationID() + "/" + currentNews.getSenderid() + "/" + "confirm";
                    CloudDataService.sendRequest(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), data, new Function1<String, Unit>() {
                        @Override
                        public Unit invoke(String s) {
                            ((DashboardActivity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new Thread(() -> {
                                        sendOneSignalNotification(currentNews.getSenderid(), SharedPrefManager.read(SharedPrefManager.USER_NAME, ""));
                                    }).start();
                                    notifications.remove(position);
                                    notifyDataSetChanged();
                                }
                            });
                            return null;
                        }
                    });
                }
            });

            holder.cancelFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String data = currentNews.getNotificationID() + "/" + currentNews.getSenderid() + "/" + "cancel";
                            CloudDataService.sendRequest(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), data, new Function1<String, Unit>() {
                                @Override
                                public Unit invoke(String s) {
                                    ((DashboardActivity)mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifications.remove(position);
                                            notifyDataSetChanged();
                                        }
                                    });
                                    return null;
                                }
                            });
                        }
                    }).start();
                }
            });
        } else if (notifications.get(position).getNotificationType().contains("projectComment")) {
            holder.parent.setOnClickListener(v -> {
                if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
                    if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equalsIgnoreCase("vg")) {
                        fetchProjects(currentNews);
                    } else {
                        fetchDeals(currentNews);
                    }
                } else {
                    if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg")) {
                        fetchProjects(currentNews);
                    } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("hmg")) {
                        fetchDeals(currentNews);
                    } else {
                        fetchProjects(currentNews);
                    }
                }
            });
        }
    }

    private void fetchProjects(NotificationsModel currentNews) {
        try {
            for (ProjectDetails projectDetails: SharedInstance.getProjectCatalog().getProjectCatalog()) {
                if (projectDetails.getProjectID().equals(currentNews.getSenderid())) {
                    String title = projectDetails.getTitle();
                    String desc = projectDetails.getDescription();
                    String thumbnail = "";
                    try {
                        thumbnail = ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + projectDetails.image;
                    } catch (Exception e) {

                    }
                    String budget = projectDetails.getBudget();
                    String investment = projectDetails.getInvestment();
                    News news = new News(title, "Business", investment, "", ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + "/projects/" + projectDetails.projectID, thumbnail, budget, desc, projectDetails.projectAssets);
                    com.techno.vginv.ProjectDetails.open(mContext, news);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchDeals(NotificationsModel currentNews) {
        try {
            for (ProjectDetails projectDetails: SharedInstance.getProjectCatalog().getDealsCatalog()) {
                if (projectDetails.getProjectID().equals(currentNews.getSenderid())) {
                    String title = projectDetails.getTitle();
                    String desc = projectDetails.getDescription();
                    String thumbnail = "";
                    try {
                        thumbnail = ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + projectDetails.image;
                    } catch (Exception e) {

                    }
                    String budget = projectDetails.getBudget();
                    String investment = projectDetails.getInvestment();
                    News news = new News(title, "Business", investment, "", ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + "/projects/" + projectDetails.projectID, thumbnail, budget, desc, projectDetails.projectAssets);
                    com.techno.vginv.ProjectDetails.open(mContext, news);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;
        private CircleImageView thumbnailImageView;
        private LinearLayout notificationlayout;
        private CardView parent;
        private ImageView addFriend, cancelFriend;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            thumbnailImageView = itemView.findViewById(R.id.thumbnail);
            notificationlayout = itemView.findViewById(R.id.notification_layout);
            parent = itemView.findViewById(R.id.card_view);
            addFriend = itemView.findViewById(R.id.add_friend);
            cancelFriend = itemView.findViewById(R.id.cancel_friend);
        }
    }

    private void sendOneSignalNotification(String userId, String userName) {
        try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic Yzc1M2Y4NTAtZjNkNC00NDExLWE5NTgtNTdjMWIwMmQzN2Nl");
            con.setRequestMethod("POST");

            String strJsonBody = "{"
                    +   "\"app_id\": \"af0bb11e-f674-47a8-8718-bc1c78c36019\","
                    +   "\"android_channel_id\": \"163157c3-fe29-49c0-b9bf-f713dc59eff4\","
                    +   "\"include_external_user_ids\": ["+ userId +"],"
                    +   "\"data\": {\"foo\": \"bar\"},"
                    +   "\"headings\": {\"en\": \"Friend Request Accepted \"},"
                    +   "\"contents\": {\"en\": \" " + userName + " has accepted your friend request\"}"
                    + "}";


            System.out.println("strJsonBody:\n" + strJsonBody);

            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            System.out.println("httpResponse: " + httpResponse);

            if (  httpResponse >= HttpURLConnection.HTTP_OK
                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            System.out.println("jsonResponse:\n" + jsonResponse);
            con.disconnect();

        } catch(Throwable t) {
            t.printStackTrace();
        }
    }
}
