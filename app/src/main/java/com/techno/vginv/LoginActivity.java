package com.techno.vginv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText emailID, password;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailID = findViewById(R.id.email);
        password = findViewById(R.id.password);
        dialog = new ProgressDialog(this);
    }

    public void onLoginPressed(View view) {
        if (emailID.getText().toString().isEmpty()) {
            Toast.makeText(this, "Email ID is required", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.setMessage(getResources().getString(R.string.pleasewait));
        dialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", emailID.getText().toString());
            jsonObject.put("password", password.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        CloudDataService.userLogin(jsonObject, () -> {
            try {
                if (SharedPrefManager.read(SharedPrefManager.IS_LOGGEDIN, false)) {
//                    SharedPrefManager.delete();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    SharedPrefManager.write(SharedPrefManager.EMAIL, emailID.getText().toString());
                    SharedPrefManager.write(SharedPrefManager.PASSWORD, password.getText().toString());

                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                } else {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Email or Password is invalid", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
