package com.gianteyes.gaarigarmechanic.services;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static APIClient instance;
    private final Api myApi;
    private String token;

    private APIClient() {

        final OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(final Chain chain) throws IOException {
                final Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        final Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.myApi = retrofit.create(Api.class);
    }

    public static synchronized APIClient getInstance() {
        if (APIClient.instance == null) {
            APIClient.instance = new APIClient();
        }
        return APIClient.instance;
    }

    public Api getMyApi() {
        return this.myApi;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
