package com.rendall.martyn.lightup.http;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Martyn
 * on 07/02/2016.
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = "http://www.martyn.tech:8182/";
    public static final String API_BASE_URL_LOCAL = "http://192.168.1.200:8182/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static <S> S createServiceRemote(Class<S> serviceClass) {

        return createService(serviceClass, API_BASE_URL);
    }

    public static <S> S createServiceLocal(Class<S> serviceClass) {

        return createService(serviceClass, API_BASE_URL_LOCAL);
    }

    public static <S> S createService(Class<S> serviceClass, String apiUrl) {

        httpClient.addInterceptor(new AuthenticationInterceptor("martyn", "shamrockin642"));

        OkHttpClient client = httpClient.build();

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(JacksonConverterFactory.create())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .client(client)
                        .build();

        return retrofit.create(serviceClass);
    }
}
