package com.KeoBuaBao.Entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// This room for now is available for only two players

/**
 * A class to implement the room entity
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @author Nguyen Van Trang
 */
@Entity
@Setter @Getter @RequiredArgsConstructor()
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String host; // The host of the room. There is only one host in each room.
    private String players = ""; // All players (username) in the room, separated by a blank space
    private String disconnectedPlayers = ""; // Save the players (username) been disconnected from the server
    private String timersSinceDisconnected = ""; // Save the time disconnection for each disconnected player respectively
    private String playerOne; // Seat (position) one of a game
    private String playerTwo; // Seat (position) two of a game
}
