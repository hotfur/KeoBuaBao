package com.KeoBuaBao.Utility;

import java.util.ArrayList;
import java.util.List;

public class DetermineResult {
    private static void playerOneWin(List<String> resultList) {
        resultList.set(0, "+");
        resultList.set(1, "-");
    }

    private static void playerTwoWin(List<String> resultList) {
        resultList.set(0, "-");
        resultList.set(1, "+");
    }

    public static List<String> announceResult(long playerOneChoice, long playerTwoChoice) {
        List<String> resultList = new ArrayList<String>();
        resultList.add("0");
        resultList.add("0");
        if(playerOneChoice == playerTwoChoice)
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
