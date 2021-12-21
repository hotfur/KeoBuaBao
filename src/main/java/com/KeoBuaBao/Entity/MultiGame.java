package com.KeoBuaBao.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MultiGame {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "multiGame", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("multiGame")
    private List<PlayerMultiGame> PlayerMultiGame = new ArrayList<PlayerMultiGame>();

    private String dateTime;
    private Long timePerMove;
    private Long numberRounds;
    private String resultOne;
    private String resultTwo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getNumberRounds() {
        return numberRounds;
    }

    public void setNumberRounds(Long numberRounds) {
        this.numberRounds = numberRounds;
    }

    public String getResultOne() {
        return resultOne;
    }

    public void setResultOne(String resultOne) {
        this.resultOne = resultOne;
    }

    public String getResultTwo() {
        return resultTwo;
    }

    public void setResultTwo(String resultTwo) {
        this.resultTwo = resultTwo;
    }

    public List<com.KeoBuaBao.Entity.PlayerMultiGame> getPlayerMultiGame() {
        return PlayerMultiGame;
    }

    public void setPlayerMultiGame(List<com.KeoBuaBao.Entity.PlayerMultiGame> playerMultiGame) {
        PlayerMultiGame = playerMultiGame;
    }
}
