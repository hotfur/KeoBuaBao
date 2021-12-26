package com.KeoBuaBao.Utility;

import java.util.Date;

public class DateUtilis {
    public static long getCurrentDate() {
        Date date = new Date();
        return date.getTime();
    }

    //If the user last activity is yesterday then he must login again.
    //Date1 is the last activity of the user in database, date2 is the login timestamp.
    public static boolean isTokenExpired(long date1, long date2) {
        return date1 + 86400000 > date2;
    }


    //If the user last login is less than one day and he is active in the last hour, approve
    //renewal of the auth token
    public static boolean eligibleToRenew(long date1, long date2) {
        return date1 + 3600000 > getCurrentDate() && getCurrentDate() < 86400000 + date2;
    }
}
