package com.rendall.martyn.lightup.services;

import android.util.Log;

public class ControllerOnService extends ControllerService {

    protected static final String TAG = "ControllerOnService";

    public ControllerOnService() {
        super("ControllerOnService");
    }

    @Override
    void sendCommand(String controllerId) {
        Log.i(TAG, "Starting sendCommand");

        service.controllerSwitchOn(controllerId).enqueue(this);

        Log.i(TAG, "Finished sendCommand");
    }
}
