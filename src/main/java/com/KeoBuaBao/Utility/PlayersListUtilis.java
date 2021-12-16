package com.KeoBuaBao.Utility;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayersListUtilis {
    public static List<String> getAllPlayers(String allPlayersStr) {
        if(allPlayersStr.isEmpty())
            return new ArrayList<String>();

        String[] allPlayersArray = allPlayersStr.split(" ");
        List<String> allPlayerList = new ArrayList<String>();
        for(int i = 0; i < allPlayersArray.length; i++)
            allPlayerList.add(allPlayersArray[i]);
        return allPlayerList;
    }
}
