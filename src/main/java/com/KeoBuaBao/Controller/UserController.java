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
     * Checking for restricted character in sequence
     * @param seq the sequence to be checked for restricted characters
     * @param notAllowedChars a string containing restricted characters
     * @param object the description of the sequence
     * @return an error response entity if the sequence contains restricted character, otherwise null
     */
    private ResponseEntity<Response> rejectRestrictedChars(String seq, String notAllowedChars, String object) {
        for (int i = 0; i < seq.length(); i++)
            // find the character from the user that matches restricted characters
            if (notAllowedChars.indexOf(seq.charAt(i)) != -1)
                return Errors.NotImplemented("The following characters are not allowed in " + object + " :" + notAllowedChars);
        return null;
    }

    private Object userCheck(User user) {
        // Check null token
        if(user.getToken() == null)
            return Errors.NotImplemented("Token cannot be null");

        // Check null datetime
        if(user.getStatus() == null)
            return Errors.NotImplemented("Datetime cannot be null");

        // Check null username
        if(user.getUsername() == null)
            return Errors.NotFound("user");

        // Find the corresponding user information with the given username.
        List<User> foundUser = userRepository.findByUsername(user.getUsername());
        // Catch an error when the user cannot be found
        if(foundUser.isEmpty()) return Errors.NotFound("user");

        User currentUser = foundUser.get(0); // Get the corresponding user record
        //Check for deleted account
        if (currentUser.isDeleted()) return Errors.NotImplemented("This user has permanently deleted their account");
        // Check equal token
        if (DateUtilis.isTokenExpired(currentUser.getStatus(), user.getStatus())) return Errors.Expired("token");
        String serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), user.getStatus());
        // Remember not implement the request when the tokens do not match
        if(!serverToken.equals(user.getToken()))
            return Errors.NotImplemented("Tokens do not match");
        currentUser.setStatus(DateUtilis.getCurrentDate());
        userRepository.save(currentUser);
        return currentUser;
    }
    
    /**
     * Return a response entity error for a unique field (username, password, etc.) been taken
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
     * Admin use only
     * A helper API for the admin to get all the users from the user entity
     * @return a list consisting of all users
     */
    @GetMapping("")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * An API to get one user information
     * @param user the user information: username and token authentication
     * @return a response entity represents a given user information
     */
    @GetMapping("/get_one_user")
    public ResponseEntity<Response> getOneUser(@RequestBody User user) {
        // Perform some basic user checking
        Object check = userCheck(user);
        if (check instanceof ResponseEntity) return (ResponseEntity<Response>) check;
        User currentUser = ((User) check);

        return Success.WithData("This user is found", currentUser);
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

        String username = user.getUsername();
        // Check invalid username: No space
        var validUsernameResponse = rejectRestrictedChars(username," ", "username");
        if (validUsernameResponse != null) return validUsernameResponse;

        // Find username: This step checks username duplication
        var foundUser = userRepository.findByUsername(user.getUsername());
        if(foundUser.size() > 0) return TakenError("Username");

        // Find email: This step checks email duplication
        var foundEmail = userRepository.findByEmail(user.getEmail());
        if(foundEmail.size() > 0) return TakenError("Email");

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
     * both token resemble, the server will change the user information.
     * The user cannot change his username.
     * @param user new user record to be updated
     * @return a response entity represents whether the update is successful or not.
     */
    @PutMapping("")
    public ResponseEntity<Response> updateUser(@RequestBody User user) {
        // Perform some basic user checking
        Object check = userCheck(user);
        if (check instanceof ResponseEntity) return (ResponseEntity<Response>) check;
        User currentUser = ((User) check);

        // Update all the fields appropriately
        if(user.getEmail() != null)
            currentUser.setEmail(user.getEmail());
        if(user.getPassword() != null)
            currentUser.setPassword(SecurityUtils.hashPassword(user.getPassword()));
        if(user.getAvatar() != null)
            currentUser.setAvatar(user.getAvatar());
        if(user.getSkinColor() != null)
            currentUser.setSkinColor(user.getSkinColor());
        if(user.getTimePerMove() != null)
            currentUser.setTimePerMove(user.getTimePerMove());
        if(user.getNumberRound() != null)
            currentUser.setNumberRound(user.getNumberRound());
        if(user.getDifficulty() != null)
            currentUser.setDifficulty(user.getDifficulty());

        userRepository.save(currentUser); // Save the updated record to the database
        return Success.WithData("Update user successfully", currentUser);
    }

    /**
     * An API to delete the user. Initially, the server should check the user's token from the client. If both token
     * resemble, the server will change the user information.
     * @param user the user information, which are the token and the status
     * @return The successful message or error
     */
    @DeleteMapping("")
    public ResponseEntity<Response> deleteUser(@RequestBody User user) {
        // Perform some basic user checking
        Object check = userCheck(user);
        if (check instanceof ResponseEntity) return (ResponseEntity<Response>) check;
        User currentUser = ((User) check);
        if (currentUser.getRoom()!=null) return Errors.NotImplemented("Please quit the game room before deleting your account");
        
        currentUser.setEmail(null);
        currentUser.setPassword(null);
        currentUser.setDeleted(true);
        userRepository.save(currentUser);
        return Success.NoData("Delete user successfully");
    }

    /**
     * An API to prompt the user login to the system by comparing either username or password
     * This API can also issue a new token for user who wants to renew his token.
     * @param user the user's username and password
     * @return a response entity represents whether the query is successful or not. If success, also return the user's
     * record as the data.
     */
    @PostMapping("/checkPassword")
    public ResponseEntity<Response> checkPassword(@RequestBody User user) {
        if(user.getUsername() == null) return Errors.NotFound("username");
        List<User> foundUser = userRepository.findByUsername(user.getUsername());
        if(foundUser.isEmpty()) return Errors.NotFound("username");

        User currentUser = foundUser.get(0);

        //Check for deleted account
        if (currentUser.isDeleted()) return Errors.NotImplemented("This user has permanently deleted their account");
        // Save the current time so generated token and datetime do not mismatch
        Long sys_time = DateUtilis.getCurrentDate();

        // Auto-renew token if eligible.
        if (user.getStatus() != null &&
                user.getToken() != null && 
                DateUtilis.eligibleToRenew(currentUser.getStatus(), user.getStatus()) &&
                user.getToken().equals(SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), user.getStatus())))
        {
                currentUser.setStatus(sys_time);
                userRepository.save(currentUser);
                currentUser.setToken(SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), sys_time));
                return Success.WithData("Correct", currentUser);
        }

        if (user.getPassword() == null) return Errors.NotImplemented("Password cannot be empty");
        // Otherwise, authenticate using traditional password
        if (SecurityUtils.hashPassword(user.getPassword())
                .equals(currentUser.getPassword())) {
            currentUser.setStatus(sys_time);
            userRepository.save(currentUser);
            currentUser.setToken(SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), sys_time));
            return Success.WithData("Correct", currentUser);
        }

        return Errors.NotImplemented("Incorrect username or password");
    }
}


