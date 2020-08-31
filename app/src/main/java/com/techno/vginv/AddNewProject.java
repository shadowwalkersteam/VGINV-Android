package com.techno.vginv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.techno.vginv.Adapters.AddAttachmentsAdapter;
import com.techno.vginv.Adapters.AddFriendsAdapter;
import com.techno.vginv.Adapters.AttachmentsAdapter;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.Model.CitiesData;
import com.techno.vginv.Model.CountriesData;
import com.techno.vginv.Model.DepartmentsData;
import com.techno.vginv.Model.ProjectAssets;
import com.techno.vginv.Views.EmptyRecyclerView;
import com.techno.vginv.data.fixtures.MessagesFixtures;
import com.techno.vginv.data.model.Message;
import com.techno.vginv.features.demo.def.DefaultDialogsActivity;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.FilePath;
import com.techno.vginv.utils.SharedPrefManager;
import com.techno.vginv.utils.SimpleResponse;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class AddNewProject extends AppCompatActivity {
    private Button submit, addImage, addFile;
    private EditText title, investment, budget, description;
    private Spinner legalStatus, country, city, category, period;

    private int countryID = 0;
    private int cityID = 0;
    private int departmentID = 0;
    private List<String> countriesList = new ArrayList<>();
    private List<String> cityList = new ArrayList<>();
    private SimpleResponse<Uri> simpleResponse;
    String picturePath = "";
    String filePath = "";
    String imageName = "";
    String fileName = "";
    private ProgressDialog dialog;
    private TextView heading;
    private TextView periodHeading;
    private String userType = "";
    private String dealsProjects = "";
    private AddAttachmentsAdapter mAdapter = null;
    public static ArrayList<ProjectAssets> projectAssetsArrayList = new ArrayList<>();

//    private String blockCharacterSet = "~#^|$%&*!.,/><:;+=-_";
    private String blockCharacterSet = "~`!@#$%\\^&*()+=-[]';,/{}|\":<>?.";

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

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

//    public AddNewProject() {
//
//    }
//
//    public AddNewProject(String category) {
//        this.dealsProjects = category;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_project);
        submit = findViewById(R.id.submit);
        addFile = findViewById(R.id.addFile);
        addImage = findViewById(R.id.addImage);
        title = findViewById(R.id.title);
        investment = findViewById(R.id.investment);
        description = findViewById(R.id.description);
        budget = findViewById(R.id.budget);
        legalStatus = findViewById(R.id.legalStatus);
        country = findViewById(R.id.country);
        city = findViewById(R.id.city);
        category = findViewById(R.id.category);
        heading = findViewById(R.id.heading);
        period = findViewById(R.id.period);
        periodHeading = findViewById(R.id.periodHeading);
        dialog = new ProgressDialog(this);

        Intent intent = getIntent();
        dealsProjects = intent.getStringExtra("type");

        title.setFilters(new InputFilter[] { filter });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        EmptyRecyclerView mRecyclerView = findViewById(R.id.attachmentsView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new AddAttachmentsAdapter(this, projectAssetsArrayList);
        mRecyclerView.setAdapter(mAdapter);


//        investment.setText(getResources().getString(R.string.Total_Investment) + " SAR");
//        budget.setText(getResources().getString(R.string.budget) + " SAR");

        try {
            if (dealsProjects != null && dealsProjects.isEmpty()) {
                if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
                    userType = SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "");
                    if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equalsIgnoreCase("vg")) {
                        heading.setText(R.string.AddNewProject);
                        periodHeading.setVisibility(View.GONE);
                        period.setVisibility(View.GONE);
                    } else {
                        heading.setText(R.string.AddNewDeal);
                        periodHeading.setVisibility(View.VISIBLE);
                        period.setVisibility(View.VISIBLE);
                        setBackGroundTint(submit);
                        setBackGroundTint(addImage);
                        setBackGroundTint(addFile);
                    }
                } else {
                    userType = SharedPrefManager.read(SharedPrefManager.USER_TYPE, "");
                    if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg")) {
                        heading.setText(R.string.AddNewProject);
                        periodHeading.setVisibility(View.GONE);
                        period.setVisibility(View.GONE);
                    } else {
                        heading.setText(R.string.AddNewDeal);
                        periodHeading.setVisibility(View.VISIBLE);
                        period.setVisibility(View.VISIBLE);
                        setBackGroundTint(submit);
                        setBackGroundTint(addImage);
                        setBackGroundTint(addFile);
                    }
                }
            } else {
                if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
                    userType = SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "");
                } else {
                    userType = SharedPrefManager.read(SharedPrefManager.USER_TYPE, "");
                }
                if (dealsProjects.equalsIgnoreCase("deals")) {
                    heading.setText(R.string.AddNewDeal);
                    periodHeading.setVisibility(View.VISIBLE);
                    period.setVisibility(View.VISIBLE);
                    setBackGroundTint(submit);
                    setBackGroundTint(addImage);
                    setBackGroundTint(addFile);
                } else {
                    heading.setText(R.string.AddNewProject);
                    periodHeading.setVisibility(View.GONE);
                    period.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (CountriesData countriesData : SharedInstance.getAllCountries().getAllCountries()) {
                countriesList.add(countriesData.getName());
            }
            Collections.sort(countriesList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, countriesList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cityList);

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter2);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.legals));

        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        legalStatus.setAdapter(adapter3);

        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.category));

        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter4);

        ArrayAdapter<String> adapter5 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.period));

        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        period.setAdapter(adapter5);

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                try {
                    String cityName = cityList.get(position);
                    for (CitiesData citiesData : SharedInstance.getAllCities().getAllCities()) {
                        if (citiesData.getCityName().equals(cityName)) {
                            cityID = Integer.parseInt(citiesData.getId());
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                try {
                    if (SharedPrefManager.read(SharedPrefManager.LANGUAGE, 0) == 1 &&
                            (Locale.getDefault().getDisplayLanguage().equals("العربية") ||
                                    getResources().getConfiguration().locale.getDisplayLanguage().equalsIgnoreCase("Arabic"))) {
                        for (DepartmentsData departmentsData : SharedInstance.getAllDepartments().getAllDepartments()) {
                            if (departmentsData.getDepArName().equals(category.getSelectedItem().toString())) {
                                departmentID = Integer.parseInt(departmentsData.getId());
                                break;
                            }
                        }
                    } else {
                        for (DepartmentsData departmentsData : SharedInstance.getAllDepartments().getAllDepartments()) {
                            if (departmentsData.getDepName().equals(category.getSelectedItem().toString())) {
                                departmentID = Integer.parseInt(departmentsData.getId());
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String countryName = countriesList.get(position);

                for (CountriesData countriesData : SharedInstance.getAllCountries().getAllCountries()) {
                    if (countriesData.getName().equals(countryName)) {
                        countryID = Integer.parseInt(countriesData.getId());
                        break;
                    }
                }

                try {
                    cityList.clear();
                    for (CitiesData citiesData : SharedInstance.getAllCities().getAllCities()) {
                        if (Integer.parseInt(citiesData.getCountryID()) == countryID) {
                            cityList.add(citiesData.getCityName());
                        }
                    }
                    Collections.sort(cityList);
                    adapter2.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addImage.setOnClickListener(view -> {
            setSimpleResponse(response -> {
                System.out.println(response);
            });
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            photoPickerIntent = Intent.createChooser(photoPickerIntent, getResources().getString(R.string.choseFile));
            startActivityForResult(photoPickerIntent, 1);
        });

        addFile.setOnClickListener(view -> {
            setSimpleResponse(response -> {
                System.out.println(response);
            });
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("video/*");
            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            photoPickerIntent = Intent.createChooser(photoPickerIntent, getResources().getString(R.string.choseFile));
            startActivityForResult(photoPickerIntent, 2);
        });

        submit.setOnClickListener(view -> {
            String heading = title.getText().toString();
            String totalInvestment = investment.getText().toString();
            String totalBudget = budget.getText().toString();
            String desc = description.getText().toString();
            String catgry = category.getSelectedItem().toString();
            String legal = legalStatus.getSelectedItem().toString();
            dialog.setMessage(getResources().getString(R.string.pleasewait));
            dialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean result = CloudDataService.postProject(ConstantStrings.URLS.Cloud.POST_PROJECT,
                            SharedPrefManager.read(SharedPrefManager.TOKEN,""),
                            picturePath,
                            filePath,
                            heading,
                            legal,
                            desc,
                            cityID,
                            countryID,
                            totalBudget,
                            totalInvestment,
                            departmentID,
                            period.getSelectedItem().toString(),
                            String.valueOf(new Date().getTime()),
                            userType,
                            projectAssetsArrayList);
                    if (result) {
                        projectAssetsArrayList.clear();
                        runOnUiThread(() -> {
                            if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
                                if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equalsIgnoreCase("vg")) {
                                    new Thread(() -> sendOneSignalNotification(SharedPrefManager.read(SharedPrefManager.USER_NAME, ""), "Project")).start();
                                    Toast.makeText(AddNewProject.this, getResources().getString(R.string.ProjectPosted), Toast.LENGTH_SHORT).show();
                                } else {
                                    new Thread(() -> sendOneSignalNotification(SharedPrefManager.read(SharedPrefManager.USER_NAME, ""), "Deal")).start();
                                    Toast.makeText(AddNewProject.this, getResources().getString(R.string.ProjectPostedHMG), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg")) {
                                    new Thread(() -> sendOneSignalNotification(SharedPrefManager.read(SharedPrefManager.USER_NAME, ""), "Project")).start();
                                    Toast.makeText(AddNewProject.this, getResources().getString(R.string.ProjectPosted), Toast.LENGTH_SHORT).show();
                                } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("hmg")) {
                                    new Thread(() -> sendOneSignalNotification(SharedPrefManager.read(SharedPrefManager.USER_NAME, ""), "Deal")).start();
                                    Toast.makeText(AddNewProject.this, getResources().getString(R.string.ProjectPostedHMG), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddNewProject.this, getResources().getString(R.string.ProjectPosted), Toast.LENGTH_SHORT).show();
                                    new Thread(() -> sendOneSignalNotification(SharedPrefManager.read(SharedPrefManager.USER_NAME, ""), "Project")).start();
                                }
                            }
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            onBackPressed();
                        });
                    } else {
                        projectAssetsArrayList.clear();
                        runOnUiThread(() -> {
                            if (!SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").isEmpty()) {
                                if (SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, "").equalsIgnoreCase("vg")) {
                                    Toast.makeText(AddNewProject.this, getResources().getString(R.string.ProjectPostFail), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddNewProject.this, getResources().getString(R.string.ProjectPostFailHMG), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("vg")) {
                                    Toast.makeText(AddNewProject.this, getResources().getString(R.string.ProjectPostFail), Toast.LENGTH_SHORT).show();
                                } else if (SharedPrefManager.read(SharedPrefManager.USER_TYPE, "").equalsIgnoreCase("hmg")) {
                                    Toast.makeText(AddNewProject.this, getResources().getString(R.string.ProjectPostFailHMG), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddNewProject.this, getResources().getString(R.string.ProjectPostFail), Toast.LENGTH_SHORT).show();                                }
                            }
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            onBackPressed();
                        });
                    }
                }
            }).start();
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == 1) {
//                Uri selectedFileUri = data.getData();
                if (data.getClipData()!=null){
                    //multiple data received
                    ClipData clipData = data.getClipData();
                    for (int count =0; count<clipData.getItemCount(); count++){
                        Uri selectedFileUri = clipData.getItemAt(count).getUri();
                        try {
                            String scheme = null;
                            if (selectedFileUri != null) {
                                scheme = selectedFileUri.getScheme();
                            }
                            if (scheme != null) {
                                if (scheme.equals("file")) {
                                    fileName = selectedFileUri.getLastPathSegment();
                                }
                                else if (scheme.equals("content")) {
                                    String[] proj = { MediaStore.Images.Media.TITLE };
                                    Cursor cursor = this.getContentResolver().query(selectedFileUri, proj, null, null, null);
                                    if (cursor != null && cursor.getCount() != 0) {
                                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                                        cursor.moveToFirst();
                                        imageName = cursor.getString(columnIndex);
//                                addImage.setText(imageName);
                                    }
                                    if (cursor != null) {
                                        cursor.close();
                                    }
                                }
                            }
                            String picturePath = FilePath.getPath(this, selectedFileUri);
//                    addImage.setText(selectedFileUri.toString());
                            ProjectAssets projectAssets = new ProjectAssets();
                            projectAssets.setId("0");
                            projectAssets.setPath(selectedFileUri.toString());
                            projectAssets.setFilePath(picturePath);
                            projectAssetsArrayList.add(projectAssets);
//                    try {
//                        simpleResponse.onResponse(selectedFileUri);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    Uri selectedFileUri = data.getData();
                    try {
                        String scheme = null;
                        if (selectedFileUri != null) {
                            scheme = selectedFileUri.getScheme();
                        }
                        if (scheme != null) {
                            if (scheme.equals("file")) {
                                fileName = selectedFileUri.getLastPathSegment();
                            }
                            else if (scheme.equals("content")) {
                                String[] proj = { MediaStore.Images.Media.TITLE };
                                Cursor cursor = this.getContentResolver().query(selectedFileUri, proj, null, null, null);
                                if (cursor != null && cursor.getCount() != 0) {
                                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                                    cursor.moveToFirst();
                                    imageName = cursor.getString(columnIndex);
//                                addImage.setText(imageName);
                                }
                                if (cursor != null) {
                                    cursor.close();
                                }
                            }
                        }
                        String picturePath = FilePath.getPath(this, selectedFileUri);
//                    addImage.setText(selectedFileUri.toString());
                        ProjectAssets projectAssets = new ProjectAssets();
                        projectAssets.setId("0");
                        projectAssets.setPath(selectedFileUri.toString());
                        projectAssets.setFilePath(picturePath);
                        projectAssetsArrayList.add(projectAssets);
//                    try {
//                        simpleResponse.onResponse(selectedFileUri);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mAdapter.notifyDataSetChanged();
                }
            } else if (requestCode == 2) {
//                Uri selectedFileUri = data.getData();
                if (data.getClipData()!=null){
                    ClipData clipData = data.getClipData();
                    for (int count =0; count<clipData.getItemCount(); count++){
                        Uri selectedFileUri = clipData.getItemAt(count).getUri();
                        try {
                            String scheme = null;
                            if (selectedFileUri != null) {
                                scheme = selectedFileUri.getScheme();
                            }
                            if (scheme != null) {
                                if (scheme.equals("file")) {
                                    fileName = selectedFileUri.getLastPathSegment();
                                }
                                else if (scheme.equals("content")) {
                                    String[] proj = { MediaStore.Images.Media.TITLE };
                                    Cursor cursor = this.getContentResolver().query(selectedFileUri, proj, null, null, null);
                                    if (cursor != null && cursor.getCount() != 0) {
                                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                                        cursor.moveToFirst();
                                        fileName = cursor.getString(columnIndex);

                                    }
                                    if (cursor != null) {
                                        cursor.close();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                       String filePath = FilePath.getPath(this, selectedFileUri);
//                addFile.setText(selectedFileUri.toString());
                        ProjectAssets projectAssets = new ProjectAssets();
                        projectAssets.setId("1");
                        projectAssets.setPath(selectedFileUri.toString());
                        projectAssets.setFilePath(filePath);
                        projectAssetsArrayList.add(projectAssets);

//                        try {
//                            simpleResponse.onResponse(selectedFileUri);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    Uri selectedFileUri = data.getData();
                    try {
                        String scheme = null;
                        if (selectedFileUri != null) {
                            scheme = selectedFileUri.getScheme();
                        }
                        if (scheme != null) {
                            if (scheme.equals("file")) {
                                fileName = selectedFileUri.getLastPathSegment();
                            }
                            else if (scheme.equals("content")) {
                                String[] proj = { MediaStore.Images.Media.TITLE };
                                Cursor cursor = this.getContentResolver().query(selectedFileUri, proj, null, null, null);
                                if (cursor != null && cursor.getCount() != 0) {
                                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                                    cursor.moveToFirst();
                                    fileName = cursor.getString(columnIndex);

                                }
                                if (cursor != null) {
                                    cursor.close();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String filePath = FilePath.getPath(this, selectedFileUri);
//                addFile.setText(selectedFileUri.toString());
                    ProjectAssets projectAssets = new ProjectAssets();
                    projectAssets.setId("1");
                    projectAssets.setPath(selectedFileUri.toString());
                    projectAssets.setFilePath(filePath);
                    projectAssetsArrayList.add(projectAssets);
                    mAdapter.notifyDataSetChanged();
                }
            }
    }

    public void setSimpleResponse(SimpleResponse<Uri> simpleResponse) {
        this.simpleResponse = simpleResponse;
    }

    private void sendOneSignalNotification(String userName, String title) {
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
                    +   "\"included_segments\": [\"All\"],"
                    +   "\"data\": {\"foo\": \"bar\"},"
                    +   "\"headings\": {\"en\": \"New  " + title +"\"},"
                    +   "\"contents\": {\"en\": \" " + userName + " has posted a new " + title +".\"}"
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
