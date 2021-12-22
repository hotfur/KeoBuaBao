package com.KeoBuaBao.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @RequiredArgsConstructor
public class MultiGame {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "multiGame", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PlayerMultiGame> PlayerMultiGame = new ArrayList<PlayerMultiGame>();

    private String dateTime;
    private Long timePerMove;
    private Long numberRounds;
    private String resultOne;
    private String resultTwo;
}
