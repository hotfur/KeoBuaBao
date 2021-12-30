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

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;

    private Long dateTime = DateUtilis.getCurrentDate();
    private Long timePerMove;
    private Long numberOfRounds;
    private Long difficulty;
    private String result = "";
    private String moves = ""; // The field cannot be the list. Change to String by comma delimiter.
    private String computerMoves = "";
}
