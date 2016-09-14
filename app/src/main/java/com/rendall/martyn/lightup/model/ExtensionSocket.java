package com.rendall.martyn.lightup.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExtensionSocket {

    String name;

    @JsonProperty("device_id")
    int deviceId;

    @JsonProperty("device_type_id")
    int deviceTypeId;

    Controller controller;

    public String getName() {
        return name;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public int getDeviceTypeId() {
        return deviceTypeId;
    }

    public Controller getController() {
        return controller;
    }
}
