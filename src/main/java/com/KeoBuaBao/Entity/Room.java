package com.KeoBuaBao.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// This room for now is available for only two players
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter @Setter private String host;
    @Getter @Setter private String players;
    @Getter @Setter private String disconnectedPlayers;
    @Getter @Setter private String timersSinceDisconnected;
    @Getter @Setter private String playerOne;
    @Getter @Setter private String playerTwo;
}
