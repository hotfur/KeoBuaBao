package com.KeoBuaBao.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter @Setter @RequiredArgsConstructor()
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
    private String status; // Online/Offline x minutes ago. Compare with 30 minutes. Status also saves the Datetime call from Front end.
    private Long winSingle; // Number of win in all single matches with the computer
    private Long drawSingle; // Number of tie in all single matches with the computer
    private Long lostSingle; // Number of lost in all single matches with the computer
    private String token; // Token authentication: Not store in the database
}
