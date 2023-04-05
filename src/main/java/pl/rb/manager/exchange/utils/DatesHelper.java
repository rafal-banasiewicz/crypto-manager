package pl.rb.manager.exchange.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class DatesHelper {

    private DatesHelper() {
    }

    public static String formatDate(long timestamp) {
        var df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date(timestamp));
    }

    public static String previousDayDate(String time) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = df.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return df.format(calendar.getTime());
    }

    public static long getBeginYearTimestamp(String year) {
        return new GregorianCalendar(
                Integer.parseInt(year),
                Calendar.JANUARY,
                1,
                0,
                0,
                0
        ).getTimeInMillis();
    }

    public static long getEndYearTimestamp(String year) {
        return new GregorianCalendar(
                Integer.parseInt(year),
                Calendar.DECEMBER,
                31,
                23,
                59,
                59
        ).getTimeInMillis();
    }
}
