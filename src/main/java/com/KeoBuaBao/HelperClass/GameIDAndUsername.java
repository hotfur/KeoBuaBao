package com.KeoBuaBao.HelperClass;

import lombok.Getter;
import lombok.Setter;

/**
 * A helper class to contain the information of gameID and username, along with the token and status for authentication
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @author Nguyen Van Trang
 */
@Setter @Getter
public class GameIDAndUsername {
    private Long gameID; // ID of the game
    private String username; // Username to be queried
    private String token; // Token authentication
    private Long status; // Nearest log in attempt period
}
