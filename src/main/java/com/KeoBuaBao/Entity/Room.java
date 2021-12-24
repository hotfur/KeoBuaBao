package com.KeoBuaBao.Entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// This room for now is available for only two players
@Entity
@Setter @Getter @RequiredArgsConstructor()
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String host;
    private String players = "";
    private String disconnectedPlayers = "";
    private String timersSinceDisconnected = "";
    private String playerOne;
    private String playerTwo;
}
