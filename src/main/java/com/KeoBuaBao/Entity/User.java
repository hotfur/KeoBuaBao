package com.KeoBuaBao.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

/**
 * A class to implement the user entity
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @author Nguyen Van Trang
 */
@Entity
@Getter @Setter @RequiredArgsConstructor()
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // This is the primary key

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @JsonIgnore
    private List<SingleGame> SingleGame = new ArrayList<>(); // A list stores all the single game of the player

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @JsonIgnore
    private List<PlayerMultiGame> PlayerMultiGame = new ArrayList<>(); // A list storing all multiplayer games of the user

    private String username; // User's username
    private String email; // User's email
    private String password; // User's password
    private boolean deleted = false; // true if the account was deleted
    private Long win; // Number of win in all multiplayer matches
    private Long tie; // Number of tie in all multiplayer matches
    private Long loss; // Number of losing game in all multiplayer matches
    private Long avatar; // 1 to x according to the avatar (default profile picture)
    private Long skinColor; // 1 to x according to the skin color
    private Long timePerMove; // Time to take a decision of rock/paper/scissors
    private Long numberRound; // The number of rounds in a game
    private Long difficulty; // The difficulty of the game. It is used to play with Machine Learning

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Room room; // Link to the room that the user belongs to

    private Long status; // Online/Offline x minutes ago. Compare with 30 minutes. Status also saves the Datetime call from Front end.
    private Long winSingle; // Number of win in all single matches with the computer
    private Long drawSingle; // Number of tie in all single matches with the computer
    private Long lostSingle; // Number of lost in all single matches with the computer

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    private SingleGame currentSingleGame; // Link to the current single game that the user is playing

    @Transient
    private String token; // Token authentication: Not store in the database
    @Transient
    private int playerPosition; // The player position seat of the user, either player one or two.
    @Transient
    private Long roomID; // Room ID to join
    @Transient
    private Long move; // The moving request, either Rock, Paper, or Scissors
}
