package com.KeoBuaBao.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * A class to store data for a player's multiplayer game record.
 *
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Nguyen Van Trang
 * @author Doan Duc Nguyen Long
 */
@Entity
@Getter @Setter @RequiredArgsConstructor()
public class PlayerMultiGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private MultiGame multiGame; // Linking many player to a multigame entity

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user; // Linking many player to a multigame entity

    private String moves = "";
}
