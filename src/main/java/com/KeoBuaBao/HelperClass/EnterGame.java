package com.KeoBuaBao.HelperClass;

import lombok.Getter;
import lombok.Setter;

/**
 * A template for a player to enter the game
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @aythor Nguyen Van Trang
 */
@Setter @Getter
public class EnterGame {
    // In order to enter the game, a user should prompt four information in
    private String username; // The user's username
    private String token; // Token for authentication
    private Long status; // The most recent online status
    private int playerPosition; // The player position seat of the user, either player one or two.
}
