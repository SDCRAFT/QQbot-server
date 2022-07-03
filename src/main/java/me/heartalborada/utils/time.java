package me.heartalborada.utils;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

public class time {
    public static String getNow(){
        Calendar calendar = Calendar.getInstance(); // get current instance of the calendar
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(calendar.getTime());
    }
}
