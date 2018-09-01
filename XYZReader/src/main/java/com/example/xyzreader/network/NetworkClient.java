package com.example.xyzreader.network;

import com.example.xyzreader.BuildConfig;
import com.example.xyzreader.datamodel.Article;

import java.util.List;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetworkClient implements INetworkClientContract {

    private final XyzReaderService service;


    private static NetworkClient networkClient;

    public static NetworkClient getInstance() {
        if (networkClient == null) {
            networkClient = new NetworkClient();
        }
        return networkClient;
    }

    private NetworkClient() {
        this.service = new Retrofit.Builder()
                .baseUrl(UrlManager.BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(XyzReaderService.class);
    }

    private OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            builder.addInterceptor(logging);
        }
        return builder.build();
    }


    @Override
    public Single<List<Article>> getArticles() {
        return service.getArticles();
    }
}
