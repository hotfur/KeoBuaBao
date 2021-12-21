package com.KeoBuaBao.Repository;

import com.KeoBuaBao.Entity.PlayerMultiGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerMultiGameRepository extends JpaRepository<PlayerMultiGame, Long> {
}
