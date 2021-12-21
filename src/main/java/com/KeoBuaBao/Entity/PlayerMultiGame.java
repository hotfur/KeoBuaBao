package com.KeoBuaBao.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class PlayerMultiGame {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @Getter @Setter
    private MultiGame multiGame;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @Getter @Setter
    private User user;

    @Getter @Setter private String moves = "";
}
