package com.inam.kashtrack;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp)).toUpperCase(Locale.getDefault());
    }

    public static String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public static boolean isToday(long timestamp) {
        Calendar today = Calendar.getInstance();
        Calendar that = Calendar.getInstance();
        that.setTimeInMillis(timestamp);
        return today.get(Calendar.YEAR) == that.get(Calendar.YEAR)
                && today.get(Calendar.DAY_OF_YEAR) == that.get(Calendar.DAY_OF_YEAR);
    }

    public static String formatAmount(double amount) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        nf.setMaximumFractionDigits(0);
        return nf.format(amount);
    }
}
