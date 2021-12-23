package com.KeoBuaBao.Utility;

import java.util.List;

public class ConvertListtoString {
    public static String convertToString(List<String> list) {
        String result = "";
        for (String s : list) result += s + " ";

        return result;
    }
}
