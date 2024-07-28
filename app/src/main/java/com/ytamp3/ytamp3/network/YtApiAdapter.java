package com.ytamp3.ytamp3.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class YtApiAdapter {
    private static YtApiService ytApiService;
    private static final String BASE_URL = "https://ytamp3.com/yt/";

    public static YtApiService getYtApiService() {
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        if (ytApiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            ytApiService = retrofit.create(YtApiService.class);
        }

        return ytApiService;
    }


}
