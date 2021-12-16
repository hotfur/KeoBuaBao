package com.KeoBuaBao.Utility;

import java.util.List;

public class ConvertListtoString {
    public static String convertToString(List<String> list) {
        String result = "";
        for(int i = 0; i < list.size(); i++)
            result += list.get(i) + " ";

        return result;
    }
}
