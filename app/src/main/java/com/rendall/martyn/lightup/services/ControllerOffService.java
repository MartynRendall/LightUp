package com.rendall.martyn.lightup.services;

import android.util.Log;

public class ControllerOffService extends ControllerService {

    protected static final String TAG = "ControllerOffService";

    public ControllerOffService() {
        super("ControllerOffService");
    }

    @Override
    void sendCommand(String controllerId) {
        Log.i(TAG, "Starting sendCommand");

        service.controllerSwitchOff(controllerId).enqueue(this);

        Log.i(TAG, "Finished sendCommand");
    }
}
