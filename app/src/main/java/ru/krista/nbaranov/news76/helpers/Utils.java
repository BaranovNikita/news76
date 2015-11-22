package ru.krista.nbaranov.news76.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by n.baranov on 22.11.2015.
 */
public class Utils {
    public static String getDateTime(String datetime) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
                    Locale.ENGLISH).parse(datetime);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(date);
    }
}
