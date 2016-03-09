package fr.gerdevstudio.runningapp;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ger on 04/01/2016.
 */
public class DateUtils {

    private final static String TAG = "DateUtils";
    private static SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static Date getDate() {
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        return d;
    }

    /**
     * Converts Date to a String
     *
     * @param date
     * @return 30/12/2018 14:18
     */
    public static String dateToString(Date date) {
        String result = mDateFormat.format(date);
        return result;
    }

    /**
     * Converts a String to a Date
     *
     * @param dateString that is in format 30/12/2018 14:18
     * @return Date object
     */
    public static Date StringToDate(String dateString) {
        Date result = new Date();
        try {
            result = mDateFormat.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, "String " + dateString + " cannot be parsed.");
        }
        return result;
    }
}
