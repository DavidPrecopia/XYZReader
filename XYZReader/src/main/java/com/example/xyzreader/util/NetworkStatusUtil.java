package com.example.xyzreader.util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class NetworkStatusUtil {
    private final ConnectivityManager connectivityManager;

    private static NetworkStatusUtil networkUtil;

    public static NetworkStatusUtil getInstance(Application context) {
        if (networkUtil == null) {
            networkUtil = new NetworkStatusUtil(context);
        }
        return networkUtil;
    }

    private NetworkStatusUtil(Application context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    
    public boolean noConnection() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo == null;
    }
}
