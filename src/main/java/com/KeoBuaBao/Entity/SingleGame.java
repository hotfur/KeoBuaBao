package com.KeoBuaBao.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Entity
public class SingleGame {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private String player;
    private String dateTime;
    private Long timePerMove;
    private Long numberOfRounds;
    private Long difficulty;
    private String result;
    private String moves; // The field cannot be the list. Change to String by comma delimeter.
    private String computerMoves;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Long getTimePerMove() {
        return timePerMove;
    }

    public void setTimePerMove(Long timePerMove) {
        this.timePerMove = timePerMove;
    }

    public Long getNumberOfRounds() {
        return numberOfRounds;
    }

    public void setNumberOfRounds(Long numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

    public Long getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Long difficulty) {
        this.difficulty = difficulty;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMoves() {
        return moves;
    }

    public void setMoves(String moves) {
        this.moves = moves;
    }

    public String getComputerMoves() {
        return computerMoves;
    }

    public void setComputerMoves(String computerMoves) {
        this.computerMoves = computerMoves;
    }
}
