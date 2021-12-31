package com.KeoBuaBao.Entity;

import com.KeoBuaBao.Utility.DateUtilis;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * A class to implement the single game entity
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @author Nguyen Van Trang
 */
@Entity
@Getter @Setter @RequiredArgsConstructor()
public class SingleGame {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;

    private Long dateTime = DateUtilis.getCurrentDate(); // Current date time of the single game
    private Long timePerMove; // A time for each move in a single game
    private Long numberOfRounds; // Total rounds in a single game
    private Long difficulty; // There will be tentatively two modes. Easy with random and Hard with Machine Learning
    private String result = ""; // The result of all rounds
    private String moves = ""; // All moves played by the user
    private String computerMoves = ""; // All moves played by the computer
}
