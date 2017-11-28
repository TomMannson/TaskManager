package pl.tommmannson.taskqueue.utils;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.RequiresPermission;
//import android.support.annotation.RequiresPermission;

/**
 * Created by tomasz.krol on 2015-11-16.
 */
public class NetworkChecker {

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean hasNetworkConnection(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
