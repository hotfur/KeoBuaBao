package com.KeoBuaBao.Utility;

import java.util.List;

public class LinearSearch {
    public static int linearFind(List<String> allPlayerList, String player) {
        for(int i = 0; i < allPlayerList.size(); i++)
            if(allPlayerList.get(i).equals(player))
                return i;
        return -1;
    }
}
