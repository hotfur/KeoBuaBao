package com.KeoBuaBao.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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
    private String players; // All players (username) in the room, separated by a blank space
    private String playerOne; // Seat (position) one of a game
    private String playerTwo; // Seat (position) two of a game
    private boolean playerOneReady; // Ready status for player one (true/false)
    private boolean playerTwoReady; // Ready status for player two (true/false)

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    private MultiGame game; // The game that this room is currently playing
}
