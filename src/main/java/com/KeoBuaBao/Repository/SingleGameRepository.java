package com.KeoBuaBao.Repository;

import com.KeoBuaBao.Entity.SingleGame;
import com.KeoBuaBao.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SingleGameRepository extends JpaRepository<SingleGame, Long> {
}
