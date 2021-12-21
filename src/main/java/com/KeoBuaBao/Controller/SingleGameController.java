package com.KeoBuaBao.Controller;

import com.KeoBuaBao.HelperClass.DetailResult;
import com.KeoBuaBao.HelperClass.Move;
import com.KeoBuaBao.HelperClass.Response;
import com.KeoBuaBao.Entity.SingleGame;
import com.KeoBuaBao.Entity.User;
import com.KeoBuaBao.Repository.SingleGameRepository;
import com.KeoBuaBao.Repository.UserRepository;
import com.KeoBuaBao.Utility.DateUtilis;
import com.KeoBuaBao.Utility.DetermineResult;
import com.KeoBuaBao.Utility.RandomUtilis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/single_game")
public class SingleGameController {
    @Autowired
    private SingleGameRepository singleGameRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public List<SingleGame> getAllSingleGame() {
        return singleGameRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getOnePlayerSingleGame(@PathVariable Long id) {
        Optional<SingleGame> foundUserGame = singleGameRepository.findById(id);
        if(foundUserGame.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new Response("ok", "Here is all of the game from the use" , foundUserGame)
            );
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Cannot find the user", "")
            );
        }
    }

    @PostMapping("")
    public ResponseEntity<Response> createSingleGame(@RequestBody User user) {
        if (user.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Username cannot be empty", "")
            );
        }

        List<User> foundUsername = userRepository.findByUsername(user.getUsername());
        if (foundUsername.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Cannot find the user", "")
            );
        }

        SingleGame newSingleGame = new SingleGame();
        newSingleGame.setPlayer(foundUsername.get(0).getUsername());
        newSingleGame.setDateTime(DateUtilis.getCurrentDate());
        newSingleGame.setTimePerMove(foundUsername.get(0).getTimePerMove());
        newSingleGame.setNumberOfRounds(foundUsername.get(0).getNumberRound());
        newSingleGame.setDifficulty(foundUsername.get(0).getDifficulty());
        newSingleGame.setResult("");
        newSingleGame.setMoves("");
        newSingleGame.setComputerMoves("");
        singleGameRepository.save(newSingleGame);

        return ResponseEntity.status(HttpStatus.OK).body(
                new Response("ok", "New game is successfully added", "")
        );
    }

    @PostMapping("/{gameID}")
    public ResponseEntity<Response> playWithComputer(@PathVariable long gameID, @RequestBody Move playerMove) {
        long computerMove = RandomUtilis.getRandom(1L, 3L);
        Optional<SingleGame> foundSingleGame = singleGameRepository.findById(gameID);
        if(!foundSingleGame.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Cannot find the game belong to the user", "")
            );

        if(playerMove.getMove() < 1 || playerMove.getMove() > 3) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new Response("fail", "Illegal move", "")
            );
        }

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

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("done", "Game over!", detailResult)
            );
        }


        // Player one is the user whereas player two is the computer when passing.
        List<String> resultList = DetermineResult.announceResult(playerMove.getMove(), computerMove);

        SingleGame singleGame = foundSingleGame.get();
        singleGame.setMoves(singleGame.getMoves() + Long.toString(playerMove.getMove()));
        singleGame.setComputerMoves(singleGame.getComputerMoves() + Long.toString(computerMove));
        singleGame.setResult(singleGame.getResult() + resultList.get(0));
        singleGameRepository.save(singleGame);

        // Winning case: Player beats computer
        if(resultList.get(0).equals("+") && resultList.get(1).equals("-")) {
            List<User> foundUser = userRepository.findByUsername(singleGame.getPlayer());
            User currentUser = foundUser.get(0);
            currentUser.setWinSingle(currentUser.getWinSingle() + 1);
            userRepository.save(currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new Response("ok", "This move is successfully sent. Congratulation! You have won this round!", computerMove)
            );
        }

        // Losing case: Player loses computer
        else if(resultList.get(0).equals("-") && resultList.get(1).equals("+")) {
            List<User> foundUser = userRepository.findByUsername(singleGame.getPlayer());
            User currentUser = foundUser.get(0);
            currentUser.setLostSingle(currentUser.getLostSingle() + 1);
            userRepository.save(currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new Response("ok", "This move is successfully sent. Unfortunately, the computer has beaten you!", computerMove)
            );
        }

        // Game draw
        else {
            List<User> foundUser = userRepository.findByUsername(singleGame.getPlayer());
            User currentUser = foundUser.get(0);
            currentUser.setDrawSingle(currentUser.getDrawSingle() + 1);
            userRepository.save(currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new Response("ok", "This move is successfully sent. The game results in a draw!", computerMove)
            );
        }


        // Check the result to determine the status of the game. Game with finished status cannot be played.
        // Return the move from the computer.
    }
}
