package com.KeoBuaBao.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @JsonIgnore
    private List<PlayerMultiGame> PlayerMultiGame = new ArrayList<>();

    private String username;
    private String email;
    private String password;
    private Long win; // Number of win in all multiplayer matches
    private Long tie; // Number of tie in all multiplayer matches
    private Long loss; // Number of losing game in all multiplayer matches
    private Long avatar; // 1 to x according to the avatar (default profile picture)
    private Long skinColor; // 1 to x according to the skin color
    private Long timePerMove; // Time to take a decision of rock/paper/scissors
    private Long numberRound; // The number of rounds in a game
    private Long difficulty;
    private Long roomId; // The room ID that the user belongs to
    private String status; // Online/Offline x minutes ago
    private Long winSingle; // Number of win in all single matches with the computer
    private Long drawSingle; // Number of tie in all single matches with the computer
    private Long lostSingle; // Number of lost in all single matches with the computer

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getWin() {
        return win;
    }

    public void setWin(Long win) {
        this.win = win;
    }

    public Long getLoss() {
        return loss;
    }

    public void setLoss(Long loss) {
        this.loss = loss;
    }

    public Long getAvatar() {
        return avatar;
    }

    public void setAvatar(Long avatar) {
        this.avatar = avatar;
    }

    public Long getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(Long skinColor) {
        this.skinColor = skinColor;
    }

    public Long getTimePerMove() {
        return timePerMove;
    }

    public void setTimePerMove(Long timePerMove) {
        this.timePerMove = timePerMove;
    }

    public Long getNumberRound() {
        return numberRound;
    }

    public void setNumberRound(Long numberRound) {
        this.numberRound = numberRound;
    }

    public Long getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Long difficulty) {
        this.difficulty = difficulty;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getWinSingle() {
        return winSingle;
    }

    public void setWinSingle(Long winSingle) {
        this.winSingle = winSingle;
    }

    public Long getLostSingle() {
        return lostSingle;
    }

    public void setLostSingle(Long lostSingle) {
        this.lostSingle = lostSingle;
    }

    public Long getTie() {
        return tie;
    }

    public void setTie(Long tie) {
        this.tie = tie;
    }

    public Long getDrawSingle() {
        return drawSingle;
    }

    public void setDrawSingle(Long drawSingle) {
        this.drawSingle = drawSingle;
    }

    public List<com.KeoBuaBao.Entity.PlayerMultiGame> getPlayerMultiGame() {
        return PlayerMultiGame;
    }

    public void setPlayerMultiGame(List<com.KeoBuaBao.Entity.PlayerMultiGame> playerMultiGame) {
        PlayerMultiGame = playerMultiGame;
    }
}
