package com.KeoBuaBao.Repository;

import com.KeoBuaBao.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUsername(String username);
    List<User> findByEmail(String email);
    List<User> findByUsernameAndPassword(String username, String password);
}
