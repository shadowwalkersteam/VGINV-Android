package com.techno.vginv.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.techno.vginv.DashboardActivity;
import com.techno.vginv.LoginActivity;
import com.techno.vginv.R;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;

import org.json.JSONObject;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ChangeFragment extends Fragment {

    private EditText oldPass, newPass, confirmPass;
    private Button changePass;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change, container, false);
        oldPass = view.findViewById(R.id.oldPassword);
        newPass = view.findViewById(R.id.newPassword);
        confirmPass = view.findViewById(R.id.confirmPassword);
        changePass = view.findViewById(R.id.changePassword);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        changePass.setOnClickListener(view1 -> {
            String oldPassword = oldPass.getText().toString();
            String newPassword = newPass.getText().toString();
            String confirmPassword = confirmPass.getText().toString();

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(getActivity(), getResources().getString(R.string.fieldRequired), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(getActivity(), getResources().getString(R.string.passwordMatchError), Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("currentPassword", oldPassword);
                jsonObject.put("newPassword", newPassword);

                CloudDataService.changePassword(SharedPrefManager.read(SharedPrefManager.TOKEN, ""), jsonObject, s -> {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (s.isEmpty()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.passwordChangeError), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), getResources().getString(R.string.passwordChangeSuccesss), Toast.LENGTH_LONG).show();
                                SharedPrefManager.delete();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                            }
                        }
                    });
                    return null;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
