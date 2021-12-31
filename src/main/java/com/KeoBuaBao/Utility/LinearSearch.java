package com.KeoBuaBao.Utility;

import java.util.List;

/**
 * A linear search utility
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @author Nguyen Van Trang
 */
public class LinearSearch {
    /**
     * A linear search to find an element in a list implementation
     * @param allPlayerList the list consisting of all players to find
     * @param player a player needs to find in the list
     * @return the corresponding index of the player in the list. If the player is not found, return -1
     */
    public static int linearFind(List<String> allPlayerList, String player) {
        // Traverse every elements in the list. Return the index if the element is found
        for(int i = 0; i < allPlayerList.size(); i++)
            if(allPlayerList.get(i).equals(player))
                return i;
        return -1; // After iterating the loop, the element is still not be found.
    }
}
