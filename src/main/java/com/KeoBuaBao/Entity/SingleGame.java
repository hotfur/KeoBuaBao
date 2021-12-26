package com.KeoBuaBao.Entity;

import com.KeoBuaBao.Utility.DateUtilis;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter @Setter @RequiredArgsConstructor()
public class SingleGame {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String player;
    private Long dateTime = DateUtilis.getCurrentDate();
    private Long timePerMove;
    private Long numberOfRounds;
    private Long difficulty;
    private String result = "";
    private String moves = ""; // The field cannot be the list. Change to String by comma delimiter.
    private String computerMoves = "";
}
