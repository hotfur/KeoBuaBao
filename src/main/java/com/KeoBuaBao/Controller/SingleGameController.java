package com.KeoBuaBao.Controller;

import com.KeoBuaBao.Entity.SingleGame;
import com.KeoBuaBao.Entity.User;
import com.KeoBuaBao.Repository.SingleGameRepository;
import com.KeoBuaBao.Repository.UserRepository;
import com.KeoBuaBao.Responses.*;
import com.KeoBuaBao.Utility.DateUtilis;
import com.KeoBuaBao.Utility.DetermineResult;
import com.KeoBuaBao.Utility.RandomUtilis;
import com.KeoBuaBao.Utility.SecurityUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * A class controls the single game function
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @author Nguyen Van Trang
 */
@RestController
@RequestMapping("/single_game")
public class SingleGameController {
    @Autowired
    private SingleGameRepository singleGameRepository;
    @Autowired
    private UserRepository userRepository;

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
     * Get all single game has been played
     * @return a list containing all single games
     */
    @GetMapping("")
    public List<SingleGame> getAllSingleGame() {
        return singleGameRepository.findAll();
    }

    /**
     * Get all single game of a specified user
     * @param user the user wants to see
     * @return all single games related to that user
     */
    @PostMapping("/get_player_games")
    public ResponseEntity<Response> getOnePlayerSingleGame(@RequestBody User user) {
        // Perform some basic user checking
        Object check = userCheck(user);
        if (check instanceof ResponseEntity) return (ResponseEntity<Response>) check;
        User currentUser = ((User) check);

        // Get the list of all single game in the database
        List<SingleGame> foundSinglegame = currentUser.getSingleGame();
        if(!foundSinglegame.isEmpty()) {
            // Fetch them from database
            for (int i = 0; i < foundSinglegame.size(); i++) foundSinglegame.set(i, Hibernate.unproxy(foundSinglegame.get(i), SingleGame.class));
            return Success.WithData("Here is all of the game from the user", foundSinglegame);
        }
        else return Errors.NotFound("games");
    }

    /**
     * Create a new single game for a specified player
     * @param user the player wants to play new game
     * @return a response entity to notify whether the game is created successfully or not
     */
    @PostMapping("")
    public ResponseEntity<Response> createSingleGame(@RequestBody User user) {
        // Perform some basic user checking
        Object check = userCheck(user);
        if (check instanceof ResponseEntity) return (ResponseEntity<Response>) check;
        User currentUser = ((User) check);

        // Cannot start another single game when one is playing with the computer
        SingleGame currentSingleGame = currentUser.getCurrentSingleGame();
        if(currentSingleGame != null)
            return Errors.NotImplemented("You are playing with the computer");

        SingleGame newSingleGame = new SingleGame();
        newSingleGame.setUser(currentUser);
        newSingleGame.setTimePerMove(currentUser.getTimePerMove());
        newSingleGame.setNumberOfRounds(currentUser.getNumberRound());
        newSingleGame.setDifficulty(currentUser.getDifficulty());
        singleGameRepository.save(newSingleGame);
        
        List<SingleGame> listSingle = currentUser.getSingleGame();
        listSingle.add(newSingleGame);
        currentUser.setSingleGame(listSingle);

        currentUser.setCurrentSingleGame(newSingleGame);
        userRepository.save(currentUser);

        return Success.WithData("New game is successfully added", newSingleGame);
    }

    /**
     * Allow the player to make moves with a random number generator
     * @param user an entity that includes both player authentication and move information
     * @return errors if failed, match result if success
     */
    @PostMapping("/play_single")
    public ResponseEntity<Response> playWithComputer(@RequestBody User user) {
        // Perform some basic user checking
        Object check = userCheck(user);
        if (check instanceof ResponseEntity) return (ResponseEntity<Response>) check;
        User currentUser = ((User) check);

        long computerMove = RandomUtilis.getRandom(1L, 3L);

        if(user.getMove() < 1 || user.getMove() > 3) return Errors.NotImplemented("Illegal move");

        SingleGame currentSingleGame = currentUser.getCurrentSingleGame();
        if(currentSingleGame == null)
            return Errors.NotImplemented("You have to create a new game to play");

        // Player one is the user whereas player two is the computer when passing.
        var resultList = DetermineResult.announceResult(user.getMove(), computerMove);

        currentSingleGame.setMoves(currentSingleGame.getMoves() + user.getMove());
        currentSingleGame.setComputerMoves(currentSingleGame.getComputerMoves() + computerMove);
        currentSingleGame.setResult(currentSingleGame.getResult() + resultList.get(0));
        singleGameRepository.save(currentSingleGame);

        // The condition to check when the game is over
        if(currentSingleGame.getResult().length() >= currentSingleGame.getNumberOfRounds()) {
            long countWin = 0;
            long countLose = 0;
            String result = currentSingleGame.getResult();
            for(int i = 0; i < result.length(); i++) {
                if(result.charAt(i) == '+')
                    countWin++;
                else if(result.charAt(i) == '-')
                    countLose++;
            }

            // Update single record for the user
            if(countWin > countLose)
                currentUser.setWinSingle(currentUser.getWinSingle() + 1);
            else if(countWin < countLose)
                currentUser.setLostSingle(currentUser.getLostSingle() + 1);
            else
                currentUser.setDrawSingle(currentUser.getDrawSingle() + 1);

            currentUser.setCurrentSingleGame(null);
            userRepository.save(currentUser);
        }

        // Winning case: Player beats computer
        if(resultList.get(0).equals("+") && resultList.get(1).equals("-"))
            return Success.WithData("Congratulation! You have won this round!", computerMove);


        // Losing case: Player loses computer
        else if(resultList.get(0).equals("-") && resultList.get(1).equals("+"))
            return Success.WithData("Unfortunately, the computer has beaten you!", computerMove);

        // Game draw
        else
            return Success.WithData("The game results in a draw!", computerMove);
    }
}
