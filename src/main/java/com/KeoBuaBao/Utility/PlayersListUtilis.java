package com.KeoBuaBao.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayersListUtilis {
    public static List<String> getAllPlayers(String allPlayersStr) {
        if(allPlayersStr.isEmpty())
            return new ArrayList<String>();

        String[] allPlayersArray = allPlayersStr.split(" ");
        var allPlayerList = new ArrayList<String>();
        Collections.addAll(allPlayerList, allPlayersArray);
        return allPlayerList;
    }
}
