package com.KeoBuaBao.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// This room for now is available for only two players
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String host;
    private String players;
    private String disconnectedPlayers;
    private String timersSinceDisconnected;
    private String playerOne;
    private String playerTwo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPlayers() {
        return players;
    }

    public void setPlayers(String players) {
        this.players = players;
    }

    public String getDisconnectedPlayers() {
        return disconnectedPlayers;
    }

    public void setDisconnectedPlayers(String disconnectedPlayers) {
        this.disconnectedPlayers = disconnectedPlayers;
    }

    public String getTimersSinceDisconnected() {
        return timersSinceDisconnected;
    }

    public void setTimersSinceDisconnected(String timersSinceDisconnected) {
        this.timersSinceDisconnected = timersSinceDisconnected;
    }

    public String getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(String playerOne) {
        this.playerOne = playerOne;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(String playerTwo) {
        this.playerTwo = playerTwo;
    }
}
