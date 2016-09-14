package com.rendall.martyn.lightup.receivers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.rendall.martyn.lightup.Constants;
import com.rendall.martyn.lightup.R;
import com.rendall.martyn.lightup.fragments.NumberPickerPreference;
import com.rendall.martyn.lightup.geofence.GeofenceErrorMessages;
import com.rendall.martyn.lightup.services.ExtensionsAllOffService;
import com.rendall.martyn.lightup.services.ExtensionsAllOnService;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.Calendar;
import java.util.List;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.rendall.martyn.lightup.Constants.BROADCASTS.ENABLE_REMOTE;
import static com.rendall.martyn.lightup.Constants.NOTIFICATIONS.GEOFENCE_ENTERED_NOTIFICATION_ID;
import static com.rendall.martyn.lightup.Constants.SHARED_PREFS.AT_HOME_FLAG;
import static com.rendall.martyn.lightup.Constants.SHARED_PREFS.GEOFENCE_HOME_CREATED_TIME;
import static com.rendall.martyn.lightup.Constants.SHARED_PREFS.NAME;

public class GeofenceReceiver extends BroadcastReceiver {

    private static final String TAG = GeofenceReceiver.class.getSimpleName();

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, MODE_PRIVATE);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(context, geofencingEvent.getErrorCode());

            Log.e(TAG, errorMessage);

            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
                && isHomeGeofenceTriggered(geofencingEvent.getTriggeringGeofences())) {

            // Prevent event spam by ignoring further enter events if we never actually left the house
            if (!atHome(context)) {
                setAtHome(sharedPreferences);
                DateTime now = new DateTime();

                if (isWithinDarkTime(now)) {
                    displayRemoteControl(context);

                    if (geofenceWasNotJustCreated(sharedPreferences, now)) {
                        turnLightsOn();
                        sendLightsOnNotification();
                    }

                } else {
                    scheduleRemoteControlForSunset();
                }
            }

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            setAwayFromHome(sharedPreferences);

            ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(Constants.NOTIFICATIONS.REMOTE_CONTROL_NOTIFICATION_ID);

        } else {
            Log.e(TAG, context.getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    private boolean isHomeGeofenceTriggered(List<Geofence> triggeringGeofences) {

        for (Geofence geofence : triggeringGeofences) {

            if(geofence.getRequestId().equals(GeofenceCreator.HOME_REQUEST_ID)) {
                return true;
            }
        }

        return false;
    }

    //TODO method duplication
    private boolean atHome(Context context) {
        return context.getSharedPreferences(NAME, MODE_PRIVATE).getBoolean(AT_HOME_FLAG, false);
    }

    private void setAtHomeFlag(SharedPreferences sharedPreferences, boolean atHome) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(AT_HOME_FLAG, atHome);
        editor.apply();
    }

    private void setAwayFromHome(SharedPreferences sharedPreferences) {
        setAtHomeFlag(sharedPreferences, false);
    }

    private void setAtHome(SharedPreferences sharedPreferences) {
        setAtHomeFlag(sharedPreferences, true);
    }

    private boolean isWithinDarkTime(DateTime now) {

        return isAfterDarkTimeStart(now) && isBeforeCutOff(now);
    }

    private boolean isAfterDarkTimeStart(DateTime now) {

        //TODO: Duplication of sunset
        //TODO: Hard coded location, should be reusing geofence and in options pref
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(new Location("55.949031", "-2.950737"), "Europe/London");

        Calendar cal = calculator.getOfficialSunsetCalendarForDate(Calendar.getInstance());

        DateTime sunset = new DateTime(cal);

        sunset = sunset.minusMinutes(getSunsetToleranceInMinutes());

        return now.isAfter(sunset);
    }

    private boolean isBeforeCutOff(DateTime now) {

        return now.toLocalTime().isBefore(new LocalTime(23, 59, 59));
    }

    private void displayRemoteControl(Context context) {

        context.sendBroadcast(new Intent(context, RemoteControlReceiver.class));
    }

    private void scheduleRemoteControlForSunset() {

        //TODO: Duplication of sunset
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(new Location("55.949031", "-2.950737"), "Europe/London");

        Calendar cal = calculator.getOfficialSunsetCalendarForDate(Calendar.getInstance());

        DateTime sunsetTimeWithTolerance = new DateTime(cal);
        sunsetTimeWithTolerance = sunsetTimeWithTolerance.minusMinutes(getSunsetToleranceInMinutes());

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(context, RemoteControlReceiver.class);

        alarmManager.set(AlarmManager.RTC, sunsetTimeWithTolerance.getMillis(), PendingIntent.getBroadcast(context, ENABLE_REMOTE, intent, FLAG_CANCEL_CURRENT));

    }

    private int getSunsetToleranceInMinutes() {

        //apply tolerance from settings
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPref.getInt("pref_remote_start_time", NumberPickerPreference.DEFAULT_STEPPED_VALUE);
    }

    private boolean geofenceWasNotJustCreated(SharedPreferences sharedPreferences, DateTime now) {

        long geofenceCreatedTime = sharedPreferences.getLong(GEOFENCE_HOME_CREATED_TIME, 0);

        DateTime createdWithTolerance = new DateTime(geofenceCreatedTime).plusMinutes(15);

        return now.isAfter(createdWithTolerance);
    }

    private void turnLightsOn() {
        context.startService(new Intent(context, ExtensionsAllOnService.class));
    }

    private void sendLightsOnNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Intent intent = new Intent(context, ExtensionsAllOffService.class);
        intent.putExtra(Constants.NOTIFICATION_ID, GEOFENCE_ENTERED_NOTIFICATION_ID);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setColor(Color.BLUE)
                .setContentTitle("Welcome Home!")
                .setContentText("Lights have been switched on")
                .addAction(new NotificationCompat.Action(R.mipmap.ic_launcher, "Switch Off", PendingIntent.getService(context, 22, intent, PendingIntent.FLAG_UPDATE_CURRENT)));

        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(GEOFENCE_ENTERED_NOTIFICATION_ID, builder.build());
    }
}
