package com.rendall.martyn.lightup.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.rendall.martyn.lightup.R;
import com.rendall.martyn.lightup.geofence.GeofenceErrorMessages;

import java.util.Date;

import static com.rendall.martyn.lightup.Constants.SHARED_PREFS.GEOFENCE_HOME_CREATED_TIME;
import static com.rendall.martyn.lightup.Constants.SHARED_PREFS.NAME;

public class GeofenceCreator extends BroadcastReceiver implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    protected static final String TAG = "GeofenceCreator";

    public static final String HOME_REQUEST_ID = "LIGHTUP_HOME";

    Context context;
    GoogleApiClient mGoogleApiClient;
    PendingIntent geofencePendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        registerAndConnect();
    }

    public void registerAndConnect() {

        if (mGoogleApiClient == null) {
            buildGoogleApiClient(context);
        }

        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.i(TAG, "Connected to GoogleApiClient");

        // TODO remotely log all the gefence set up and trigger stuff to aid debugging
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);

            SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            long createdTime = new Date().getTime();
            editor.putLong(GEOFENCE_HOME_CREATED_TIME, createdTime);
            editor.commit();

            Log.e(TAG, "Created home_created: " + createdTime);

        } catch (SecurityException securityException) {
            Log.e(TAG, "Invalid location permission. You need to use ACCESS_FINE_LOCATION with geofences", securityException);
        }

        mGoogleApiClient.disconnect();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.addGeofence(populateHomeGeofenceList());
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);

        return builder.build();
    }

    public Geofence populateHomeGeofenceList() {

        // TODO set via settings HOME location
        return new Geofence.Builder()
                .setRequestId(HOME_REQUEST_ID)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setCircularRegion(
                        55.949031,
                        -2.950737,
                        200)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private PendingIntent getGeofencePendingIntent() {

        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        } else {
            Intent intent = new Intent("com.rendall.martyn.lightup.broadcast.ACTION_RECEIVE_GEOFENCE");

            return PendingIntent.getBroadcast(context, 12345, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.e(TAG, "Geofence successfully added");
            //Toast.makeText(context, context.getString(R.string.add_geofence), Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, GeofenceErrorMessages.getErrorString(context, status.getStatusCode()));
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: code=" + connectionResult.getErrorCode());
        Toast.makeText(context, "Failed to connect to play services", Toast.LENGTH_LONG).show();
    }
}
