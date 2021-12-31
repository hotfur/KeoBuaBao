package com.KeoBuaBao.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * An utility class to determine the result of a rock paper scissor match
 *
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @author Nguyen Van Trang
 */
public class DetermineResult {
    /**
     * A helper method to return the result if player one wins the game
     * @param resultList a list of two elements represent the win and lose corresponded with "+" and "-" sign respectively
     */
    private static void playerOneWin(List<String> resultList) {
        resultList.set(0, "+");
        resultList.set(1, "-");
    }

    /**
     * A helper method to return the result if player two wins the game
     * @param resultList a list of two elements represent the win and lose corresponded with "+" and "-" sign respectively
     */
    private static void playerTwoWin(List<String> resultList) {
        resultList.set(0, "-");
        resultList.set(1, "+");
    }

    /**
     * A utility method to show the result of the match given the move of two players
     * @param playerOneChoice move related to player one
     * @param playerTwoChoice move related to player two
     * @return a list of two elements represent the win, draw, and lose corresponded with "+", "0", and "-" sign
     * respectively
     */
    public static List<String> announceResult(long playerOneChoice, long playerTwoChoice) {
        List<String> resultList = new ArrayList<String>();
        resultList.add("0");
        resultList.add("0");

        // Draw case
        if(playerOneChoice == playerTwoChoice)
            return resultList;

        // The match has to end with a win or a loss case
        else {
            // Scissors vs Rock or Scissors vs Paper
            if(playerOneChoice == 1) {
                if(playerTwoChoice == 2)
                    playerTwoWin(resultList);
                else if(playerTwoChoice == 3)
                    playerOneWin(resultList);
            }

            // Rock vs Scissors or Rock vs Paper
            else if(playerOneChoice == 2) {
                if(playerTwoChoice == 1)
                    playerOneWin(resultList);
                else if(playerTwoChoice == 3)
                    playerTwoWin(resultList);
            }

            // Paper vs Scissors or Paper vs Rock
            else if(playerOneChoice == 3) {
                if(playerTwoChoice == 1)
                    playerTwoWin(resultList);
                else if(playerTwoChoice == 2)
                    playerOneWin(resultList);
            }
        }

        return resultList;
    }
}
