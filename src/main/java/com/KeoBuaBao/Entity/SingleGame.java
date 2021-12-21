package com.KeoBuaBao.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Entity
public class SingleGame {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter @Setter private String player;
    @Getter @Setter private String dateTime;
    @Getter @Setter private Long timePerMove;
    @Getter @Setter private Long numberOfRounds;
    @Getter @Setter private Long difficulty;
    @Getter @Setter private String result;
    @Getter @Setter private String moves; // The field cannot be the list. Change to String by comma delimeter.
    @Getter @Setter private String computerMoves;
}
