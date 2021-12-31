package com.KeoBuaBao.HelperClass;

import lombok.Getter;
import lombok.Setter;

/**
 * A helper class to represent the move as the request body for the play API
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @author Nguyen Van Trang
 */
@Getter @Setter
public class Move {
    // To send a move, a user should give the username, the move decision, and the authentication information
    private String username; // The username of the user
    private String token; // The token authentication
    private Long status; // The nearest status
    private Long move; // The moving request, either Rock, Paper, or Scissors
}
