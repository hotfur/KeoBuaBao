package com.KeoBuaBao.Utility;

import java.util.ArrayList;
import java.util.List;

public class DetermineResult {
    private static void playerOneWin(List<Integer> resultList) {
        resultList.set(0, 1);
        resultList.set(1, -1);
    }

    private static void playerTwoWin(List<Integer> resultList) {
        resultList.set(0, -1);
        resultList.set(1, 1);
    }

    public static List<Integer> announceResult(int playerOneChoice, int playerTwoChoice) {
        List<Integer> resultList = new ArrayList<Integer>();
        resultList.add(0);
        resultList.add(0);
        if(playerOneChoice == playerOneChoice)
            return resultList;
        else {
            if(playerOneChoice == 1) {
                if(playerTwoChoice == 2)
                    playerTwoWin(resultList);
                else if(playerTwoChoice == 3)
                    playerOneWin(resultList);
            }

            else if(playerOneChoice == 2) {
                if(playerTwoChoice == 1)
                    playerOneWin(resultList);
                else if(playerTwoChoice == 3)
                    playerTwoWin(resultList);
            }

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
