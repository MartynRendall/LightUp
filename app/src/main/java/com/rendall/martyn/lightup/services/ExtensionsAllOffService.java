package com.rendall.martyn.lightup.services;

import android.util.Log;

public class ExtensionsAllOffService extends ExtensionService {

    protected static final String TAG = "ExtensionsAllOffService";

    public ExtensionsAllOffService() {
        super("ExtensionsAllOffService");
    }

    @Override
    void sendCommand() {
        Log.i(TAG, "Starting sendCommand");

        service.allExtensionsOff().enqueue(this);

        Log.i(TAG, "Finished sendCommand");
    }
}
