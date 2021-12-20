package com.KeoBuaBao.Repository;

import com.KeoBuaBao.Entity.MultiGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MultiGameRepository extends JpaRepository<MultiGame, Long> {
}
