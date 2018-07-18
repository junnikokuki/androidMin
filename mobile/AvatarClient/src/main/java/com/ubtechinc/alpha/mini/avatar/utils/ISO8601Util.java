package com.ubtechinc.alpha.mini.avatar.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by ubt on 2017/4/24.
 */

public class ISO8601Util {
    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss aa dd/MM/yyyy", Locale.US);
    private static final SimpleDateFormat sDateFormat1 = new SimpleDateFormat("hh:mm:ss aa MM/dd/yyyy", Locale.US);
    /** Transform Calendar to ISO 8601 string. */
    public static String fromCalendar(final Calendar calendar) {
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    /** Get current date and time formatted as ISO 8601 string. */
    public static String now() {
        return fromCalendar(GregorianCalendar.getInstance());
    }

    /** Transform ISO 8601 string to Calendar. */
    public static Calendar toCalendar(final String iso8601string)
            throws ParseException {
        Calendar calendar = GregorianCalendar.getInstance();
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid length", 0);
        }
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
        calendar.setTime(date);
        return calendar;
    }

    public static String toLocalTime(final String iso8601string) {
        try {
            Calendar calendar = toCalendar(iso8601string);
            Date date = calendar.getTime();
            return sDateFormat1.format(date);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
