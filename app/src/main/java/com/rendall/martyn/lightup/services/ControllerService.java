package com.rendall.martyn.lightup.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.rendall.martyn.lightup.Constants;
import com.rendall.martyn.lightup.http.ServiceGenerator;
import com.rendall.martyn.lightup.http.ExtensionSocketAPI;
import com.rendall.martyn.lightup.model.Controller;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ControllerService extends IntentService implements Callback<Controller> {

    protected static final String TAG = "ControllerService";

    ExtensionSocketAPI service;

    public ControllerService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "Starting onHandleIntent");

        service = ServiceGenerator.createServiceRemote(ExtensionSocketAPI.class);

        sendCommand(intent.getStringExtra(Constants.CONTROLLER_ID));

        Log.i(TAG, "Finished onHandleIntent");
    }

    abstract void sendCommand(String controllerId);

    @Override
    public void onResponse(Call<Controller> call, Response<Controller> response) {
        Log.i(TAG, "onResponse: Success? " + response.isSuccessful());

        if (!response.isSuccessful()) {
            Toast.makeText(this, "Failed: " + response.code(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<Controller> call, Throwable t) {
        Log.i(TAG, "onFailure");

        Toast.makeText(this, "Had a failure", Toast.LENGTH_SHORT).show();
    }
}
