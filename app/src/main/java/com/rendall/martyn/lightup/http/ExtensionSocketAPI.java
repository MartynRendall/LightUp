package com.rendall.martyn.lightup.http;

import com.rendall.martyn.lightup.model.Controller;
import com.rendall.martyn.lightup.model.ExtensionSocket;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;


/**
 * Created by Martyn on 03/02/2016.
 */
public interface ExtensionSocketAPI {

    @GET("devices/sockets/extensions")
    Call<List<ExtensionSocket>> getAlExtensionSockets();

    @GET("devices/sockets/extensions")
    Observable<List<ExtensionSocket>> getAlExtensionSockets2();

    @GET("devices/sockets/extensions/1")
    Call<ExtensionSocket> getExtensionSocket();

    @PUT("/controllers/{controller_id}/on")
    Call<Controller> controllerSwitchOn(@Path("controller_id") String controllerId);

    @PUT("/controllers/{controller_id}/off")
    Call<Controller> controllerSwitchOff(@Path("controller_id") String controllerId);

    @PUT("/devices/sockets/extensions/off")
    Call<List<ExtensionSocket>> allExtensionsOff();

    @PUT("/devices/sockets/extensions/on")
    Call<List<ExtensionSocket>> allExtensionsOn();
}
