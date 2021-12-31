package com.KeoBuaBao.Controller;

import com.KeoBuaBao.Entity.SingleGame;
import com.KeoBuaBao.Entity.User;
import com.KeoBuaBao.HelperClass.*;
import com.KeoBuaBao.Repository.SingleGameRepository;
import com.KeoBuaBao.Repository.UserRepository;
import com.KeoBuaBao.Responses.*;
import com.KeoBuaBao.Utility.DateUtilis;
import com.KeoBuaBao.Utility.DetermineResult;
import com.KeoBuaBao.Utility.RandomUtilis;
import com.KeoBuaBao.Utility.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    @GetMapping("/get_player_single_game")
    public ResponseEntity<Response> getOnePlayerSingleGame(@RequestBody User user) {
        // Check null token
        if(user.getToken() == null)
            return Errors.NotImplemented("Token cannot be null");

        // Check null datetime
        if(user.getStatus() == null)
            return Errors.NotImplemented("Datetime cannot be null");

        List<User> foundUser = userRepository.findByUsername(user.getUsername());
        if(foundUser.isEmpty())
            return Errors.NotFound("username");

        User currentUser = foundUser.get(0);
        // Check equal token
        if (DateUtilis.isTokenExpired(currentUser.getStatus(), user.getStatus())) return Errors.Expired("token");
        String serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), user.getStatus());
        if(!serverToken.equals(user.getToken()))
            return Errors.NotImplemented("Tokens do not match");
        currentUser.setStatus(DateUtilis.getCurrentDate());

        // Get the list of all single game in the database
        List<SingleGame> foundSinglegame = currentUser.getSingleGame();
        if(!foundSinglegame.isEmpty())
            return Success.WithData("Here is all of the game from the user" , foundSinglegame);
        else
            return Errors.NotFound("user");
    }

    /**
     * Create a new single game for a specified player
     * @param user the player wants to play new game
     * @return a response entity to notify whether the game is created successfully or not
     */
    @PostMapping("")
    public ResponseEntity<Response> createSingleGame(@RequestBody User user) {
        if (user.getUsername() == null) return Errors.NotFound("user");

        // Check null token
        if(user.getToken() == null)
            return Errors.NotImplemented("Token cannot be null");

        // Check null datetime
        if(user.getStatus() == null)
            return Errors.NotImplemented("Datetime cannot be null");

        List<User> foundUsername = userRepository.findByUsername(user.getUsername());
        if (foundUsername.isEmpty()) return Errors.NotFound("user");

        User currentUser = foundUsername.get(0);
        // Check equal token
        if (DateUtilis.isTokenExpired(currentUser.getStatus(), user.getStatus())) return Errors.Expired("token");
        String serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), user.getStatus());
        if(!serverToken.equals(user.getToken()))
            return Errors.NotImplemented("Tokens do not match");
        currentUser.setStatus(DateUtilis.getCurrentDate());

        SingleGame newSingleGame = new SingleGame();
        newSingleGame.setUser(currentUser);
        newSingleGame.setTimePerMove(foundUsername.get(0).getTimePerMove());
        newSingleGame.setNumberOfRounds(foundUsername.get(0).getNumberRound());
        newSingleGame.setDifficulty(foundUsername.get(0).getDifficulty());
        singleGameRepository.save(newSingleGame);
        
        List<SingleGame> listSingle = currentUser.getSingleGame();
        listSingle.add(newSingleGame);
        currentUser.setSingleGame(listSingle);
        userRepository.save(currentUser);

        return Success.WithData("New game is successfully added", newSingleGame);
    }

    /**
     * Allow the player to make moves with a random number generator
     * @param gameID the single player game id
     * @param playerMove an entity that includes both player authentication and move information
     * @return errors if failed, match result if success
     */
    @PostMapping("/{gameID}")
    public ResponseEntity<Response> playWithComputer(@PathVariable long gameID, @RequestBody Move playerMove) {

        // Check null token
        if(playerMove.getToken() == null)
            return Errors.NotImplemented("Token cannot be null");

        // Check null datetime
        if(playerMove.getStatus() == null)
            return Errors.NotImplemented("Datetime cannot be null");

        List<User> foundUser = userRepository.findByUsername(playerMove.getUsername());
        if(foundUser.isEmpty())
            return Errors.NotFound("user");

        User currentUser = foundUser.get(0);
        // Check equal token
        if (DateUtilis.isTokenExpired(currentUser.getStatus(), playerMove.getStatus())) return Errors.Expired("token");
        String serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), playerMove.getStatus());
        if(!serverToken.equals(playerMove.getToken()))
            return Errors.NotImplemented("Tokens do not match");
        currentUser.setStatus(DateUtilis.getCurrentDate());

        long computerMove = RandomUtilis.getRandom(1L, 3L);
        Optional<SingleGame> foundSingleGame = singleGameRepository.findById(gameID);
        if(!foundSingleGame.isPresent()) return Errors.NotFound("game belong to the user");

        if(playerMove.getMove() < 1 || playerMove.getMove() > 3) return Errors.NotImplemented("Illegal move");

        SingleGame currentSingleGame = foundSingleGame.get();
        if(currentSingleGame.getResult().length() >= currentSingleGame.getNumberOfRounds()) {
            long countWin = 0;
            long countDraw = 0;
            long countLose = 0;
            String result = currentSingleGame.getResult();
            for(int i = 0; i < result.length(); i++) {
                if(result.charAt(i) == '+')
                    countWin++;
                else if(result.charAt(i) == '-')
                    countLose++;
                else if(result.charAt(i) == '0')
                    countDraw++;
            }

            DetailResult detailResult = new DetailResult(countWin, countDraw, countLose);

            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new Response("done", "Game over!", detailResult)
            );
        }


        // Player one is the user whereas player two is the computer when passing.
        var resultList = DetermineResult.announceResult(playerMove.getMove(), computerMove);

        SingleGame singleGame = foundSingleGame.get();
        singleGame.setMoves(singleGame.getMoves() + playerMove.getMove());
        singleGame.setComputerMoves(singleGame.getComputerMoves() + computerMove);
        singleGame.setResult(singleGame.getResult() + resultList.get(0));
        singleGameRepository.save(singleGame);

        // Winning case: Player beats computer
        if(resultList.get(0).equals("+") && resultList.get(1).equals("-")) {
            currentUser.setWinSingle(currentUser.getWinSingle() + 1);
            userRepository.save(currentUser);

            return Success.WithData("Congratulation! You have won this round!", computerMove);
        }

        // Losing case: Player loses computer
        else if(resultList.get(0).equals("-") && resultList.get(1).equals("+")) {
            currentUser.setLostSingle(currentUser.getLostSingle() + 1);
            userRepository.save(currentUser);

            return Success.WithData("Unfortunately, the computer has beaten you!", computerMove);
        }

        // Game draw
        else {
            currentUser.setDrawSingle(currentUser.getDrawSingle() + 1);
            userRepository.save(currentUser);

            return Success.WithData("The game results in a draw!", computerMove);
        }


        // Check the result to determine the status of the game. Game with finished status cannot be played.
        // Return the move from the computer.
    }
}
