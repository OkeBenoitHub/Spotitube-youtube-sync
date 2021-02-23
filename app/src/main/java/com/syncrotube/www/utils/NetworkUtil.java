package com.syncrotube.www.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import com.syncrotube.www.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Network Util :: contain every recurring task dealing with Network
 */
final public class NetworkUtil {

    /**
     * Check if user is connected to internet
     * @return true or false
     */
    public static boolean isUserConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static boolean checkForInternetAvailability(Context context) {
        if (!isUserConnectedToNetwork(context)) {
            MainUtil.showToastMessage(context, context.getString(R.string.No_internet_available_text));
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check if any network is available
     * @param context :: context
     * @return :: true or false
     */
    private static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = null;
            if (connectivityManager != null) {
                nw = connectivityManager.getActiveNetwork();
            }
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = null;
            if (connectivityManager != null) {
                nwInfo = connectivityManager.getActiveNetworkInfo();
            }
            return nwInfo != null && nwInfo.isConnectedOrConnecting();
        }
    }

    /**
     * Get internet connectivity status
     * @return status string
     */
    public static String getConnectivityStatusString(Context context) {
        String status = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                status = context.getString(R.string.wifi_enabled_text);
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                status = context.getString(R.string.mobile_data_enabled_text);
            }
        } else {
            status = context.getString(R.string.No_internet_available_text);
        }
        return status;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
