package com.KeoBuaBao.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @JsonIgnore
    @Getter @Setter
    private List<PlayerMultiGame> PlayerMultiGame = new ArrayList<>();

    @Getter @Setter private String username;
    @Getter @Setter private String email;
    @Getter @Setter private String password;
    @Getter @Setter private Long win; // Number of win in all multiplayer matches
    @Getter @Setter private Long tie; // Number of tie in all multiplayer matches
    @Getter @Setter private Long loss; // Number of losing game in all multiplayer matches
    @Getter @Setter private Long avatar; // 1 to x according to the avatar (default profile picture)
    @Getter @Setter private Long skinColor; // 1 to x according to the skin color
    @Getter @Setter private Long timePerMove; // Time to take a decision of rock/paper/scissors
    @Getter @Setter private Long numberRound; // The number of rounds in a game
    @Getter @Setter private Long difficulty;
    @Getter @Setter private Long roomId; // The room ID that the user belongs to
    @Getter @Setter private String status; // Online/Offline x minutes ago
    @Getter @Setter private Long winSingle; // Number of win in all single matches with the computer
    @Getter @Setter private Long drawSingle; // Number of tie in all single matches with the computer
    @Getter @Setter private Long lostSingle; // Number of lost in all single matches with the computer
}
