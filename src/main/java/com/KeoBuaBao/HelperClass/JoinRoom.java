package com.KeoBuaBao.HelperClass;

import lombok.Getter;
import lombok.Setter;

/**
 * A helper class for the join room API request body
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @author Nguyen Van Trang
 */
@Setter @Getter
public class JoinRoom {
    // To join the room, the user needs to give the username, roomID to join, as well as the authentication including
    // token and the nearest status
    private String username; // Username of the user
    private String token; // Token authentication
    private Long status; // Nearest status from the client
    private Long roomID; // Room ID to join
}
