package com.rendall.martyn.lightup.http;

import android.util.Base64;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Martyn on 07/02/2016.
 */
public class AuthenticationInterceptor implements Interceptor {

    private String username;
    private String password;

    public AuthenticationInterceptor(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        String credentials = this.username + ":" + this.password;

        final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        Request.Builder requestBuilder = original.newBuilder()
                .header("Authorization", basic)
                .header("Accept", "applicaton/json")
                .method(original.method(), original.body());

        Request request = requestBuilder.build();

        return chain.proceed(request);
    }
}
