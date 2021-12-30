package com.KeoBuaBao.Repository;

import com.KeoBuaBao.Entity.SingleGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SingleGameRepository extends JpaRepository<SingleGame, Long> {
}
