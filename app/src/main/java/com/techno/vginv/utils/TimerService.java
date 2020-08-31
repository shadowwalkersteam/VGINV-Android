package com.techno.vginv.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Worker {
    private boolean isSessionExpired = false;
    public static final String TASK_DESC = "task_desc";
    private Timer timer;
    private SimpleResponse<String> simpleResponse;

    public TimerService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
//        checkForSession();
//        Data output = new Data.Builder()
//                .putString(TASK_DESC, "session expired")
//                .build();
        return Result.success();
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
                                    String newTime = jsonObject.getString("updated_at");
                                    if (!newTime.equalsIgnoreCase(SharedPrefManager.read(SharedPrefManager.SESSION_TIME, ""))) {
                                        isSessionExpired = true;
                                        if (simpleResponse != null) {
                                            simpleResponse.onResponse("session expired");
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

    public SimpleResponse<String> getSimpleResponse() {
        return simpleResponse;
    }

    public void setSimpleResponse(SimpleResponse<String> simpleResponse) {
        this.simpleResponse = simpleResponse;
    }
}
