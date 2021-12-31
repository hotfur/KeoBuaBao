package com.KeoBuaBao.Utility;

import java.util.List;

/**
 * Utility class to convert all elements in the list to a long string seperated by a space
 *
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @author Nguyen Van Trang
 */
public class ConvertListtoString {
    /**
     * Convert all elements in the list to a long string seperated by a space
     * @param list the given input list
     * @return a string consisting of all elements in the list, seperated by a space
     */
    public static String convertToString(List<String> list) {
        String result = "";
        for (String s : list)
            result += s + " ";

        return result;
    }
}
