package com.KeoBuaBao.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An utility to convert a string to array list separated by blank space
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @author Nguyen Van Trang
 */
public class PlayersListUtilis {
    public static List<String> getAllPlayers(String allPlayersStr) {
        // Corner case: If there is an empty string, return an empty array list
        if(allPlayersStr.isEmpty())
            return new ArrayList<String>();

        // Convert string to array separated by blank space
        String[] allPlayersArray = allPlayersStr.split(" ");
        var allPlayerList = new ArrayList<String>();
        // Convert array to array list
        Collections.addAll(allPlayerList, allPlayersArray);
        return allPlayerList;
    }
}
