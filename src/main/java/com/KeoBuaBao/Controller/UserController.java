package com.KeoBuaBao.Controller;

import com.KeoBuaBao.Responses.Errors;
import com.KeoBuaBao.HelperClass.Response;
import com.KeoBuaBao.Entity.User;
import com.KeoBuaBao.Responses.Success;
import com.KeoBuaBao.Repository.UserRepository;
import com.KeoBuaBao.Utility.RandomUtilis;
import com.KeoBuaBao.Utility.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    //Return an error if an object is already taken
    private static ResponseEntity<Response> TakenError(String object) {
        return Errors.NotImplemented(object + " is already taken");
    }
    //Return an error if an object is empty
    private static ResponseEntity<Response> EmptyError(String object) {
        return Errors.NotImplemented(object + " cannot be empty");
    }


    // API get all users
    @GetMapping("")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // API get one user
    @GetMapping("{id}")
    public ResponseEntity<Response> getOneUser(@PathVariable long id) {
        Optional<User> foundUser = userRepository.findById(id);
        if(foundUser.isPresent()) return Success.WithData("This user is found", foundUser);
        else return Errors.NotFound("user");
    }

    // API insert user
    @PostMapping("")
    public ResponseEntity<Response> addUser(@RequestBody User user) {
        // Check null username
        if(user.getUsername() == null) return EmptyError("Username");

        // Check null password
        if(user.getPassword() == null) return EmptyError("Password");

        // Check null email
        if(user.getEmail() == null) return EmptyError("Email");

        // Find username
        var foundUser = userRepository.findByUsername(user.getUsername());
        if(foundUser.size() > 0) return TakenError("Username");

        // Find email
        var foundEmail = userRepository.findByEmail(user.getEmail());
        if(foundEmail.size() > 0) return TakenError("Email");

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
            user.setTimePerMove(RandomUtilis.getRandom(1L, 10L));
        if(user.getNumberRound() == null)
            user.setNumberRound(RandomUtilis.getRandom(1L, 10L));
        if(user.getDifficulty() == null)
            user.setDifficulty(RandomUtilis.getRandom(1L, 10L));

        // Hash the password
        user.setPassword(SecurityUtils.hashPassword(user.getPassword()));
        userRepository.save(user);

        return Success.WithData("Add user successfully", user);
    }

//    // API update (upsert): Update if found, otherwise insert
//    @PutMapping("/{id}")
//    public ResponseEntity<Response> updateUser(@PathVariable long id, @RequestBody User newUser) {
//        // Check null username
//        if(newUser.getUsername() == null) {
//            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
//                    new Response("fail", "Username cannot be empty", "")
//            );
//        }
//
//        // Check null password
//        if(newUser.getPassword() == null) {
//            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
//                    new Response("fail", "Password cannot be empty", "")
//            );
//        }
//
//        // Check null email
//        if(newUser.getEmail() == null) {
//            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
//                    new Response("fail", "Email cannot be empty", "")
//            );
//        }
//
//        User updatedUser = userRepository.findById(id)
//                .map(user -> {
//                    user.setUsername(newUser.getUsername());
//                    user.setPassword(SecurityUtils.hashPassword(user.getPassword()));
//                    user.setWin(newUser.getWin());
//                    user.setTie(newUser.getTie());
//                    user.setLoss(newUser.getLoss());
//                    user.setAvatar(newUser.getAvatar());
//                    user.setSkinColor(newUser.getSkinColor());
//                    user.setTimePerMove(newUser.getTimePerMove());
//                    user.setNumberRound(newUser.getNumberRound());
//                    user.setDifficulty(newUser.getDifficulty());
//                    user.setRoomId(newUser.getRoomId());
//                    user.setStatus(newUser.getStatus());
//                    user.setWinSingle(newUser.getWinSingle());
//                    user.setDrawSingle(newUser.getDrawSingle());
//                    user.setLostSingle(newUser.getLostSingle());
//                    return userRepository.save(user);
//                }).orElseGet(() -> {
//                    newUser.setId(id);
//                    return userRepository.save(newUser);
//                });
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new Response("ok", "Update user successfully", updatedUser)
//        );
//    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateUser(@PathVariable Long id, @RequestBody User newUser) {
        // Find username
        var foundUsername = userRepository.findByUsername(newUser.getUsername());
        if(foundUsername.size() > 0) return TakenError("Username");

        // Find email
        var foundEmail = userRepository.findByEmail(newUser.getEmail());
        if(foundEmail.size() > 0) return TakenError("Email");


        Optional<User> foundUser = userRepository.findById(id);
        if(!foundUser.isPresent()) return Errors.NotFound("user");

        User currentUser = foundUser.get();

        // Update all the fields appropriately
        if(newUser.getUsername() != null)
            currentUser.setUsername(newUser.getUsername());
        if(newUser.getEmail() != null)
            currentUser.setEmail(newUser.getEmail());
        if(newUser.getPassword() != null)
            currentUser.setPassword(SecurityUtils.hashPassword(newUser.getPassword()));
        if(newUser.getAvatar() != null)
            currentUser.setAvatar(newUser.getAvatar());
        if(newUser.getSkinColor() != null)
            currentUser.setSkinColor(newUser.getSkinColor());
        if(newUser.getTimePerMove() != null)
            currentUser.setTimePerMove(newUser.getTimePerMove());
        if(newUser.getNumberRound() != null)
            currentUser.setNumberRound(newUser.getNumberRound());
        if(newUser.getDifficulty() != null)
            currentUser.setDifficulty(newUser.getDifficulty());

        userRepository.save(currentUser); // Save the updated record to the database
        return Success.WithData("Update user successfully", currentUser);
    }

    // API delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteUser(@PathVariable Long id) {
        if(userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return Success.NoData("Delete user successfully");
        }
        return Errors.NotFound("user");
    }
}
