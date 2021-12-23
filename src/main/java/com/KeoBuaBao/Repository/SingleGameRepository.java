package com.KeoBuaBao.Repository;

import com.KeoBuaBao.Entity.SingleGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SingleGameRepository extends JpaRepository<SingleGame, Long> {
    List<SingleGame> findByPlayer(String player);
}
