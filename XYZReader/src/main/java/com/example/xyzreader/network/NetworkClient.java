package com.example.xyzreader.network;

import android.app.Application;

import com.example.xyzreader.BuildConfig;
import com.example.xyzreader.datamodel.Article;

import java.util.List;
import java.util.Locale;

import io.reactivex.Single;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetworkClient implements INetworkClientContract {

    private final XyzReaderService service;

    // 24 hours
    private static final int CACHE_MAX_AGE = 86400;
    private static final int CACHE_MAX_STALE = CACHE_MAX_AGE;


    private static NetworkClient networkClient;

    public static NetworkClient getInstance(Application application) {
        if (networkClient == null) {
            networkClient = new NetworkClient(application);
        }
        return networkClient;
    }

    private NetworkClient(Application application) {
        this.service = new Retrofit.Builder()
                .baseUrl(UrlManager.BASE_URL)
                .client(getOkHttpClient(application))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(XyzReaderService.class);
    }

    private OkHttpClient getOkHttpClient(Application application) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        cache(builder, application);
        logging(builder);
        return builder.build();
    }

    private void cache(OkHttpClient.Builder builder, Application application) {
        Cache cache = new Cache(application.getCacheDir(), 5 * 1024 * 1024);
        builder.cache(cache);
        builder.networkInterceptors().add(ADD_CACHE_CONTROL_INTERCEPTOR);
    }

    /**
     * Dangerous interceptor that rewrites the server's cache-control header.
     * In this case, I am adding, not rewriting.
     */
    private static final Interceptor ADD_CACHE_CONTROL_INTERCEPTOR = chain -> {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .header(
                        "Cache-Control",
                        String.format(Locale.US, "max-age=%d, only-if-cached, max-stale=%d", CACHE_MAX_AGE, CACHE_MAX_STALE)
                )
                .build();
    };

    private void logging(OkHttpClient.Builder builder) {
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            builder.addInterceptor(logging);
        }
    }


    @Override
    public Single<List<Article>> getArticles() {
        return service.getArticles();
    }
}
