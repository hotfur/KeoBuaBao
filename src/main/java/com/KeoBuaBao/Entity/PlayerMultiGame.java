package com.KeoBuaBao.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
public class PlayerMultiGame {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("PlayerMultiGame")
    private MultiGame multiGame;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("PlayerMultiGame")
    private User user;

    private String moves = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MultiGame getMultiGame() {
        return multiGame;
    }

    public void setMultiGame(MultiGame multiGame) {
        this.multiGame = multiGame;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMoves() {
        return moves;
    }

    public void setMoves(String moves) {
        this.moves = moves;
    }
}
