package com.KeoBuaBao.Utility;

import java.util.Date;

/**
 * An utility class to deal with date and time
 */
public class DateUtilis {
    private static final long DAY_LONG_MILISECOND = 86400000;
    private static final long ONE_HOUR = 3600000;

    /**
     * Get the current date and time information
     * @return date and time information, as a Date class in Java
     */
    public static long getCurrentDate() {
        Date date = new Date(); // Use built-in Date class in Java
        return date.getTime();
    }

    /**
     * Determine whether the token is expired or not. If the user last activity is yesterday then he or she must login
     * again.
     *
     * @param date1 the last activity of the user in database
     * @param date2 the login timestamp.
     * @return true iff the token is expired. The user needs to re-login
     */
    public static boolean isTokenExpired(long date1, long date2) {
        return date1 + DAY_LONG_MILISECOND < date2;
    }

    /** Determine whether the token is able to generate a new one. If the user last login is less than one day and he
     * is active in the last hour, approve renewal of the auth token.
     *
     * @param date1 the last activity of the user in database
     * @param date2 the login timestamp.
     * @return true iff the user's token satisfies the condition to renew.
     */

    public static boolean eligibleToRenew(long date1, long date2) {
        return date1 + ONE_HOUR > getCurrentDate() && getCurrentDate() < DAY_LONG_MILISECOND + date2;
    }
}
