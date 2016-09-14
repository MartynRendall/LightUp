package com.rendall.martyn.lightup.services;

import android.util.Log;

public class ExtensionsAllOnService extends ExtensionService {

    protected static final String TAG = "ExtensionsAllOnService";

    public ExtensionsAllOnService() {
        super("ExtensionsAllOnService");
    }

    @Override
    void sendCommand() {
        Log.i(TAG, "Starting sendCommand");

        service.allExtensionsOn().enqueue(this);

        Log.i(TAG, "Finished sendCommand");
    }
}
