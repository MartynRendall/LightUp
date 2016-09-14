package com.rendall.martyn.lightup.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Martyn on 07/02/2016.
 */
public class Controller {

    @JsonProperty("controller_id")
    private String controllerId;

    @JsonProperty("on_command")
    private int[] onCommand;

    @JsonProperty("off_command")
    private int[] offCommand;

    public String getControllerId() {
        return controllerId;
    }

    public int[] getOnCommand() {
        return onCommand;
    }

    public int[] getOffCommand() {
        return offCommand;
    }
}
