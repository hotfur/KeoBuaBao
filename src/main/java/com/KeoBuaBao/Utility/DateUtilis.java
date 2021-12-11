package com.KeoBuaBao.Utility;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtilis {
    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }
}
