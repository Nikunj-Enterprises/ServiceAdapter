package com.easyapper.serviceadapter.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class EpochConverter {
    private static final String timezone = "Europe/Athens";

    public static String epochToDateString(long epoch, String dateFormat){
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone(timezone));
        calendar.setTimeInMillis(epoch);

        DateFormat formater = new SimpleDateFormat(dateFormat);
        Date date = calendar.getTime();

        return formater.format(date);
    }

    public static String epochToDateString(long epoch){
        return epochToDateString(epoch,"dd/MM/yyyy");
    }

    public static long dateStringToEpoch(String dateStr, String dateFormat)
            throws ParseException {
        DateFormat formater = new SimpleDateFormat(dateFormat);
        formater.getCalendar().setTimeZone(TimeZone.getTimeZone(timezone));

        Date date = formater.parse(dateStr);
        return date.getTime();
    }

    public static long getCurrentEpoch(){
        return Calendar.getInstance(TimeZone.getTimeZone(timezone)).getTimeInMillis();
    }
}
