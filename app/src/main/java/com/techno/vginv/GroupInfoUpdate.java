package com.techno.vginv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.techno.vginv.Adapters.AddFriendsAdapter;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.Model.AllUsersData;
import com.techno.vginv.Model.Friends;
import com.techno.vginv.Views.EmptyRecyclerView;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.FilePath;
import com.techno.vginv.utils.SharedPrefManager;
import com.techno.vginv.utils.SimpleResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupInfoUpdate extends AppCompatActivity {
    private EditText groupName, groupDescription;
    private Button addImage, submit;
    private SimpleResponse<Uri> simpleResponse;
    String picturePath = "";
    String imageName = "";
    String fileName = "";
    private ProgressDialog dialog;
    private static String roomID = "";
    private static String name = "";
    private static String description = "";

    public static void open(Context context, String roomId, String groupName, String groupDes) {
        roomID = roomId;
        name = groupName;
        description = groupDes;
        context.startActivity(new Intent(context, GroupInfoUpdate.class));
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

    private void setBackGroundTint(Button button) {
        Drawable buttonDrawable = button.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.red));
        button.setBackground(buttonDrawable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info_update);
        groupName = findViewById(R.id.groupName);
        groupDescription = findViewById(R.id.groupDescription);
        addImage = findViewById(R.id.addImage);
        submit = findViewById(R.id.submit);
        dialog = new ProgressDialog(this);

        groupName.setText(name);
        groupDescription.setText(description);

        if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
            if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equalsIgnoreCase("vg")) {
                setBackGroundTint(addImage);
                setBackGroundTint(submit);
            }
        } else {
            if (!SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg")) {
                setBackGroundTint(addImage);
                setBackGroundTint(submit);
            }
        }

        addImage.setOnClickListener(view -> {
            setSimpleResponse(response -> {
                System.out.println(response);
            });
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("*/*");
            photoPickerIntent = Intent.createChooser(photoPickerIntent, getResources().getString(R.string.choseFile));
            startActivityForResult(photoPickerIntent, 1);
        });

        submit.setOnClickListener(v -> {
            String name = groupName.getText().toString();
            String description = groupDescription.getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(GroupInfoUpdate.this, getResources().getString(R.string.fieldRequired), Toast.LENGTH_SHORT).show();
                return;
            }
            dialog.setMessage(getResources().getString(R.string.pleasewait));
            dialog.show();

            CloudDataService.updateRoom(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), picturePath, name, description, roomID);

            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(() -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    finish();
                    onBackPressed();
                });
            }).start();
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == 1) {
                Uri selectedFileUri = data.getData();
                try {
                    String scheme = null;
                    if (selectedFileUri != null) {
                        scheme = selectedFileUri.getScheme();
                    }
                    if (scheme != null) {
                        if (scheme.equals("file")) {
                            fileName = selectedFileUri.getLastPathSegment();
                        } else if (scheme.equals("content")) {
                            String[] proj = {MediaStore.Images.Media.TITLE};
                            Cursor cursor = this.getContentResolver().query(selectedFileUri, proj, null, null, null);
                            if (cursor != null && cursor.getCount() != 0) {
                                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                                cursor.moveToFirst();
                                imageName = cursor.getString(columnIndex);
                            }
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    }
                    picturePath = FilePath.getPath(this, selectedFileUri);
                    addImage.setText(selectedFileUri.toString());
                    try {
                        simpleResponse.onResponse(selectedFileUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    public void setSimpleResponse(SimpleResponse<Uri> simpleResponse) {
        this.simpleResponse = simpleResponse;
    }
}