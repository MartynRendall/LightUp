package com.rendall.martyn.lightup;

/**
 * Created by Martyn on 16/03/2016.
 */
public class Constants {

    public static final String CONTROLLER_ID = "CONTROLLER_ID";
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";

    public class NOTIFICATIONS {
        public static final int GEOFENCE_ENTERED_NOTIFICATION_ID = 12;
        public static final int REMOTE_CONTROL_NOTIFICATION_ID = 13;
    }

    public class SHARED_PREFS {
        public static final String NAME = "HOME_GEOFENCE";
        public static final String AT_HOME_FLAG = "AT_HOME_FLAG";
        public static final String GEOFENCE_HOME_CREATED_TIME = "HOME_CREATED";
    }

    public class BROADCASTS {
        public static final int ENABLE_REMOTE = 87;
    }
}
