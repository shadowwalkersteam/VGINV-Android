package com.techno.vginv.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techno.vginv.AddGroupMembers;
import com.techno.vginv.CreateGroupActivity;
import com.techno.vginv.DashboardActivity;
import com.techno.vginv.Fragments.AllUserProfile;
import com.techno.vginv.Model.Friends;
import com.techno.vginv.R;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class AddFriendsAdapter extends RecyclerView.Adapter<AddFriendsAdapter.ViewHolder>{
    private Context mContext;
    private List<Friends> friendsList;
    private boolean isCreateGroup = false;
    private boolean updateRoomMembers = false;
    private FrameLayout frame;

    public AddFriendsAdapter(Context context, List<Friends> newsList, boolean createGroup, boolean updateMembers, FrameLayout frameLayout) {
        mContext = context;
        friendsList = newsList;
        isCreateGroup = createGroup;
        updateRoomMembers = updateMembers;
        frame = frameLayout;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_friends_items, parent, false);
        return new AddFriendsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Friends currentNews = friendsList.get(position);
        holder.name.setText(currentNews.getName());
        holder.desgination.setText(currentNews.getDesignation());
        if (isCreateGroup) {
            holder.setIsRecyclable(false);
            if (CreateGroupActivity.friendsList.contains(Integer.valueOf(currentNews.getId()))) {
                holder.addFriends.setImageResource(R.drawable.check);
            } else {
                holder.addFriends.setImageResource(R.drawable.plus_border);
            }
        }
        else if (updateRoomMembers) {
            holder.setIsRecyclable(false);
            if (AddGroupMembers.friendsList.contains(Integer.valueOf(currentNews.getId()))) {
                holder.addFriends.setImageResource(R.drawable.check);
            } else {
                holder.addFriends.setImageResource(R.drawable.plus_border);
            }
        } else {
            holder.addFriends.setImageResource(R.drawable.plus_border);
        }
        if (currentNews.getProfilePicture().endsWith("male.png")) {
            Glide.with(mContext.getApplicationContext())
                    .load(R.drawable.male)
                    .into(holder.thumbnailImageView);
        } else {
            Glide.with(mContext.getApplicationContext())
                    .load(currentNews.getProfilePicture())
                    .into(holder.thumbnailImageView);
        }

        holder.addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isCreateGroup) {
                        if (!CreateGroupActivity.friendsList.contains(Integer.valueOf(currentNews.getId()))) {
                            CreateGroupActivity.friendsList.add(Integer.valueOf(currentNews.getId()));
                            holder.addFriends.setImageResource(R.drawable.check);
                        } else {
                            CreateGroupActivity.friendsList.remove(Integer.valueOf(currentNews.getId()));
                            holder.addFriends.setImageResource(R.drawable.plus_border);
                        }
                    }
                    else if (updateRoomMembers) {
                        if (!AddGroupMembers.friendsList.contains(Integer.valueOf(currentNews.getId()))) {
                            AddGroupMembers.friendsList.add(Integer.valueOf(currentNews.getId()));
                            holder.addFriends.setImageResource(R.drawable.check);
                        } else {
                            CreateGroupActivity.friendsList.remove(Integer.valueOf(currentNews.getId()));
                            holder.addFriends.setImageResource(R.drawable.plus_border);
                        }
                    }
                    else {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("friendId", currentNews.getId());
                        jsonObject.put("created_at", new Date().getTime());
                        CloudDataService.sendFriendRequest(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject, aBoolean -> {
                            ((DashboardActivity)mContext).runOnUiThread(() -> {
                                if (aBoolean) {
                                    holder.addFriends.setImageResource(R.drawable.check);
                                    new Thread(() -> sendOneSignalNotification(currentNews.getId(), SharedPrefManager.read(SharedPrefManager.USER_NAME, ""))).start();
                                } else {
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.friendRequestError), Toast.LENGTH_SHORT).show();
                                }
                            });
                            return null;
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.thumbnailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCreateGroup || updateRoomMembers) {
                    if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg") && currentNews.getType().equalsIgnoreCase("hmg")) {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.hmgvgerror), Toast.LENGTH_SHORT).show();
                    } else {
                        frame.setVisibility(View.VISIBLE);
                        goToFragment(new AllUserProfile(currentNews.getId(), frame), true);
                        ((AppCompatActivity) mContext).setTitle(mContext.getResources().getString(R.string.profile));
                    }
                } else {
                    if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg") && currentNews.getType().equalsIgnoreCase("hmg")) {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.hmgvgerror), Toast.LENGTH_SHORT).show();
                    } else {
                        goToFragment(new AllUserProfile(currentNews.getId(), frame), true);
                        ((AppCompatActivity) mContext).setTitle(mContext.getResources().getString(R.string.profile));
                    }
                }
            }
        });
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCreateGroup || updateRoomMembers) {
                    if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg") && currentNews.getType().equalsIgnoreCase("hmg")) {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.hmgvgerror), Toast.LENGTH_SHORT).show();
                    } else {
                        frame.setVisibility(View.VISIBLE);
                        goToFragment(new AllUserProfile(currentNews.getId(), frame), true);
                        ((AppCompatActivity) mContext).setTitle(mContext.getResources().getString(R.string.profile));
                    }
                } else {
                    if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg") && currentNews.getType().equalsIgnoreCase("hmg")) {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.hmgvgerror), Toast.LENGTH_SHORT).show();
                    } else {
                        goToFragment(new AllUserProfile(currentNews.getId(), null), true);
                        ((AppCompatActivity) mContext).setTitle(mContext.getResources().getString(R.string.profile));
                    }
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
//        private TextView vgText;
        private TextView desgination;
        private CircleImageView thumbnailImageView;
        private ImageView addFriends;
//        private LinearLayout vgTag;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contact_name);
//            vgText = itemView.findViewById(R.id.vgText);
            desgination = itemView.findViewById(R.id.designation);
            thumbnailImageView = itemView.findViewById(R.id.profile_image);
            addFriends = itemView.findViewById(R.id.add_friend);
//            vgTag = itemView.findViewById(R.id.vgTag);
        }
    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.container, fragment).commit();
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
                    +   "\"headings\": {\"en\": \"Friend Request\"},"
                    +   "\"contents\": {\"en\": \"You have a new friend request from " + userName + "\"}"
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
