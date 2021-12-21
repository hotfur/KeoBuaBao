package com.KeoBuaBao.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MultiGame {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "multiGame", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PlayerMultiGame> PlayerMultiGame = new ArrayList<PlayerMultiGame>();

    @Getter @Setter private String dateTime;
    @Getter @Setter private Long timePerMove;
    @Getter @Setter private Long numberRounds;
    @Getter @Setter private String resultOne;
    @Getter @Setter private String resultTwo;
}
