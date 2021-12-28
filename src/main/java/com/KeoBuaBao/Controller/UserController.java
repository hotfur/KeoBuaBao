package com.KeoBuaBao.Controller;

import com.KeoBuaBao.Responses.*;
import com.KeoBuaBao.Entity.User;
import com.KeoBuaBao.Repository.UserRepository;
import com.KeoBuaBao.Utility.DateUtilis;
import com.KeoBuaBao.Utility.RandomUtilis;
import com.KeoBuaBao.Utility.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Implement Rest Controller for the User entity
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @author Nguyen Van Trang
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    /**
     * Return a response entity error for a unique field (username, passowrd, etc) been taken
     * @param object a string representation for the field
     * @return a response entity with the "already taken" message
     *
     */
    private static ResponseEntity<Response> TakenError(String object) {
        return Errors.NotImplemented(object + " is already taken");
    }

    /**
     * Return a response entity error if a unique object (username, password, etc) is empty
     * @param object a string representation for the field
     * @return a response entity with the "cannot be empty" message
     */
    private static ResponseEntity<Response> EmptyError(String object) {
        return Errors.NotImplemented(object + " cannot be empty");
    }


    /**
     * A helper API for the admin to get all the users from the user entity
     * @return a list consisting of all users
     */
    @GetMapping("")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * An API to get one user information
     * @param id the user ID (this is a path variable)
     * @return a response entity represents a given user information
     */
    @GetMapping("{id}")
    public ResponseEntity<Response> getOneUser(@PathVariable long id) {
        Optional<User> foundUser = userRepository.findById(id);
        if(foundUser.isPresent())
            return Success.WithData("This user is found", foundUser);
        else
            return Errors.NotFound("user");
    }

    /**
     * A register API to a create new account
     * @param user the user initiative information. The user should input in at least username, password, and email
     * @return a response entity represents whether the query is successful or not
     */
    @PostMapping("")
    public ResponseEntity<Response> addUser(@RequestBody User user) {
        // Check null username
        if(user.getUsername() == null)
            return EmptyError("Username");

        // Check null password
        if(user.getPassword() == null)
            return EmptyError("Password");

        // Check null email
        if(user.getEmail() == null)
            return EmptyError("Email");

        // Find username: This step checks username duplication
        var foundUser = userRepository.findByUsername(user.getUsername());
        if(foundUser.size() > 0)
            return TakenError("Username");

        // Find email: This step checks email duplication
        var foundEmail = userRepository.findByEmail(user.getEmail());
        if(foundEmail.size() > 0)
            return TakenError("Email");

        // Set all the records to 0 when creating a new account
        user.setWin(0L);
        user.setLoss(0L);
        user.setWinSingle(0L);
        user.setLostSingle(0L);
        user.setDifficulty(3L);
        user.setTie(0L);
        user.setDrawSingle(0L);

        // Set the rest of the fields as random data if the user does not input them.
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

        // Hash the password (SHA-256)
        user.setPassword(SecurityUtils.hashPassword(user.getPassword()));
        user.setStatus(DateUtilis.getCurrentDate()); // Set the status to the most recent login attempt
        userRepository.save(user);

        return Success.WithData("Add user successfully", user);
    }

    /**
     * An API to update the user's information. Initially, the server should check the user's token from the client. If
     * both token resembles, the server will change the user information.
     * @param newUser new user record to be updated
     * @return a response entity represents whether the update is successful or not.
     */
    @PutMapping("")
    public ResponseEntity<Response> updateUser(@RequestBody User newUser) {
        // Check null token
        if(newUser.getToken() == null)
            return Errors.NotImplemented("Token cannot be null");

        // Check null datetime
        if(newUser.getStatus() == null)
            return Errors.NotImplemented("Datetime cannot be null");

        // Find username
        var foundUsername = userRepository.findByUsername(newUser.getUsername());
        if(foundUsername.size() > 0) return TakenError("Username");
        User currentUser = foundUsername.get(0);

        // Check equal token
        if (DateUtilis.isTokenExpired(currentUser.getStatus(), newUser.getStatus())) return Errors.Expired("token");
        String serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), newUser.getStatus());
        if(!serverToken.equals(newUser.getToken()))
            return Errors.NotImplemented("Tokens do not match");
        currentUser.setStatus(DateUtilis.getCurrentDate());

        // Update all the fields appropriately
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

    /**
     * An API to delete the user
     * @param id
     * @param user
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteUser(@PathVariable Long id, @RequestBody User user) {
        // Check null token
        if(user.getToken() == null)
            return Errors.NotImplemented("Token cannot be null");

        // Check null datetime
        if(user.getStatus() == null)
            return Errors.NotImplemented("Datetime cannot be null");

        Optional<User> foundUser = userRepository.findById(id);
        if(!foundUser.isPresent())
            return Errors.NotFound("user");

        User currentUser = foundUser.get();
        // Check equal token
        if (DateUtilis.isTokenExpired(currentUser.getStatus(), user.getStatus())) return Errors.Expired("token");
        String serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), user.getStatus());
        if(!serverToken.equals(user.getToken()))
            return Errors.NotImplemented("Tokens do not match");


        userRepository.deleteById(id);
        return Success.NoData("Delete user successfully");
    }

    // API log in
    @PostMapping("/checkPassword")
    public ResponseEntity<Response> checkPassword(@RequestBody User user) {
        if (user.getPassword() == null) return Errors.NotImplemented("Password cannot be empty");
        List<User> foundUser = userRepository.findByUsername(user.getUsername());
        if(foundUser.isEmpty()) return Errors.NotFound("username");

        User currentUser = foundUser.get(0);
        // Get server token for the user.
        String serverToken;

        //Auto-renew token if eligible.
        if (user.getStatus() != null && user.getToken() != null && DateUtilis.eligibleToRenew(currentUser.getStatus(), user.getStatus())) {
            serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), user.getStatus());
            if(user.getToken().equals(serverToken)) {
                serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), DateUtilis.getCurrentDate());
                currentUser.setStatus(DateUtilis.getCurrentDate());
                userRepository.save(currentUser);
                currentUser.setToken(serverToken);
                return Success.WithData("Correct", currentUser);
            }
        }

        // Otherwise authenticate using traditional password
        List<User> foundUserList = userRepository.findByUsernameAndPassword(user.getUsername(), SecurityUtils.hashPassword(user.getPassword()));
        if (foundUserList.size() > 0) {
            User userRecord = foundUserList.get(0);
            serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), DateUtilis.getCurrentDate());
            userRecord.setToken(serverToken);
            currentUser.setStatus(DateUtilis.getCurrentDate());
            userRepository.save(currentUser);
            return Success.WithData("Correct", userRecord);
        }

        return Errors.NotImplemented("Incorrect username or password");
    }
}


