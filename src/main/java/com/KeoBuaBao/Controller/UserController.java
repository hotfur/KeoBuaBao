package com.KeoBuaBao.Controller;

import com.KeoBuaBao.Entity.Response;
import com.KeoBuaBao.Entity.User;
import com.KeoBuaBao.Repository.UserRepository;
import com.KeoBuaBao.Utility.RandomUtilis;
import com.KeoBuaBao.Utility.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    // API get all users
    @GetMapping("")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // API get one user
    @GetMapping("{id}")
    public ResponseEntity<Response> getOneUser(@PathVariable long id) {
        Optional<User> foundUser = userRepository.findById(id);
        if(foundUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new Response("ok", "This user is found", foundUser)
            );
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Cannot find user with id = " + id, "")
            );
        }
    }

    // API insert user
    @PostMapping("")
    public ResponseEntity<Response> addUser(@RequestBody User user) {
        // Check null username
        if(user.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new Response("fail", "Username cannot be empty", "")
            );
        }

        // Check null password
        if(user.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new Response("fail", "Password cannot be empty", "")
            );
        }

        // Check null email
        if(user.getEmail() == null) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new Response("fail", "Email cannot be empty", "")
            );
        }



        // Find username
        List<User> foundUser = userRepository.findByUsername(user.getUsername());
        if(foundUser.size() > 0) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new Response("fail", "Username is already taken", "")
            );
        }

        // Find email
        List<User> foundEmail = userRepository.findByEmail(user.getEmail());
        if(foundUser.size() > 0) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new Response("fail", "Email is already taken", "")
            );
        }

        user.setWin(0L);
        user.setLoss(0L);
        user.setWinSingle(0L);
        user.setLostSingle(0L);
        user.setDifficulty(3L);
        user.setTie(0L);
        user.setDrawSingle(0L);

        if(user.getAvatar() == null)
            user.setAvatar(RandomUtilis.getRandom(1L, 10L));
        if(user.getSkinColor() == null)
            user.setSkinColor(RandomUtilis.getRandom(1L, 10L));
        if(user.getTimePerMove() == null)
            user.setSkinColor(RandomUtilis.getRandom(1L, 10L));
        if(user.getNumberRound() == null)
            user.setNumberRound(RandomUtilis.getRandom(1L, 10L));
        if(user.getDifficulty() == null)
            user.setDifficulty(RandomUtilis.getRandom(1L, 10L));

        // Hash the password
        String hashedPassword = SecurityUtils.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body(
                new Response("ok", "Add user successfully", userRepository.save(user))
        );
    }

    // API update (upsert): Update if found, otherwise insert
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateUser(@PathVariable long id, @RequestBody User newUser) {
        // Check null username
        if(newUser.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new Response("fail", "Username cannot be empty", "")
            );
        }

        // Check null password
        if(newUser.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new Response("fail", "Password cannot be empty", "")
            );
        }

        // Check null email
        if(newUser.getEmail() == null) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new Response("fail", "Email cannot be empty", "")
            );
        }

        User updatedUser = userRepository.findById(id)
                .map(user -> {
                    user.setUsername(newUser.getUsername());
                    user.setPassword(SecurityUtils.hashPassword(user.getPassword()));
                    user.setWin(newUser.getWin());
                    user.setTie(newUser.getTie());
                    user.setLoss(newUser.getLoss());
                    user.setAvatar(newUser.getAvatar());
                    user.setSkinColor(newUser.getSkinColor());
                    user.setTimePerMove(newUser.getTimePerMove());
                    user.setNumberRound(newUser.getNumberRound());
                    user.setDifficulty(newUser.getDifficulty());
                    user.setRoomId(newUser.getRoomId());
                    user.setStatus(newUser.getStatus());
                    user.setWinSingle(newUser.getWinSingle());
                    user.setDrawSingle(newUser.getDrawSingle());
                    user.setLostSingle(newUser.getLostSingle());
                    return userRepository.save(user);
                }).orElseGet(() -> {
                    newUser.setId(id);
                    return userRepository.save(newUser);
                });
        return ResponseEntity.status(HttpStatus.OK).body(
                new Response("ok", "Update user successfully", updatedUser)
        );
    }

    // API delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteUser(@PathVariable Long id) {
        boolean exists = userRepository.existsById(id);
        if(exists) {
            userRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new Response("ok", "Delete user successfully", "")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new Response("fail", "Cannot find the user to delete", "")
        );
    }

    // API Log in
    @PostMapping("/checkPassword")
    public ResponseEntity<Response> checkPassword(@RequestBody User user) {
        List<User> foundUserList = userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());

        if (foundUserList.size() > 0) {
            User userRecord = foundUserList.get(0);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new Response("ok", "Correct", userRecord)
            );
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new Response("fail", "Incorrect username or password", "")
        );
    }
}
