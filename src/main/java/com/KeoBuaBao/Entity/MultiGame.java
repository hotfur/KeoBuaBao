package com.KeoBuaBao.Entity;

import com.KeoBuaBao.Utility.DateUtilis;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


/**
 * A class to store data for multiplayer games.
 *
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Nguyen Van Trang
 * @author Doan Duc Nguyen Long
 */
@Entity
@Getter @Setter @RequiredArgsConstructor
public class MultiGame {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "multiGame", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PlayerMultiGame> PlayerMultiGame = new ArrayList<PlayerMultiGame>(); // A list containing two players of a multiplayer game
    
    private Long dateTime = DateUtilis.getCurrentDate(); // Get the current time of the game
    private Long timePerMove; // The time for each move decision
    private Long numberRounds; // The number of rounds in a game
    private String resultOne = ""; // Result of player one in all rounds
    private String resultTwo = ""; // Result of player two in all rounds
    private boolean abort1 = false; // The game is aborted by player 1
    private boolean abort2 = false; // The game is aborted by player 2


}
