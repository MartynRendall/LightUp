package com.rendall.martyn.lightup.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.rendall.martyn.lightup.Constants;
import com.rendall.martyn.lightup.http.ServiceGenerator;
import com.rendall.martyn.lightup.http.ExtensionSocketAPI;
import com.rendall.martyn.lightup.model.ExtensionSocket;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ExtensionService extends IntentService implements Callback<List<ExtensionSocket>> {

    protected static final String TAG = "ExtensionService";

    ExtensionSocketAPI service;

    public ExtensionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "Starting onHandleIntent");

        cancelRunningNotification(intent);

        service = ServiceGenerator.createServiceRemote(ExtensionSocketAPI.class);

        sendCommand();

        Log.i(TAG, "Finished onHandleIntent");
    }

    private void cancelRunningNotification(Intent intent) {
        int notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, -1);

        if (notificationId != -1) {
            ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).cancel(notificationId);
        }
    }

    abstract void sendCommand();

    @Override
    public void onResponse(Call<List<ExtensionSocket>> call, Response<List<ExtensionSocket>> response) {
        Log.i(TAG, "onResponse: Success? " + response.isSuccessful());

        if (!response.isSuccessful()) {
            Toast.makeText(this, "Failed: " + response.code(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<List<ExtensionSocket>> call, Throwable t) {
        Log.i(TAG, "onFailure");

        Toast.makeText(this, "Had a failure", Toast.LENGTH_SHORT).show();
    }
}
