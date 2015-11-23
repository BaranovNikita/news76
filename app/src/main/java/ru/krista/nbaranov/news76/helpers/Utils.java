package ru.krista.nbaranov.news76.helpers;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.krista.nbaranov.news76.MainActivity;

public class Utils {
    public static String getDateTime(String datetime) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
                    Locale.ENGLISH).parse(datetime);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(date);
    }
    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) MainActivity.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}

