package com.KeoBuaBao.Controller;

import com.KeoBuaBao.Entity.Move;
import com.KeoBuaBao.Entity.Response;
import com.KeoBuaBao.Entity.SingleGame;
import com.KeoBuaBao.Entity.User;
import com.KeoBuaBao.Repository.SingleGameRepository;
import com.KeoBuaBao.Repository.UserRepository;
import com.KeoBuaBao.Utility.DateUtilis;
import com.KeoBuaBao.Utility.RandomUtilis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        singleGameRepository.save(newSingleGame);

        return ResponseEntity.status(HttpStatus.OK).body(
                new Response("ok", "New game is successfully added", "")
        );
    }

    @PostMapping("/{id}")
    public ResponseEntity<Response> playWithComputer(@PathVariable long gameID, @RequestBody Move playerMove) {
        long computerMove = RandomUtilis.getRandom(1L, 3L);
        Optional<SingleGame> foundSingleGame = singleGameRepository.findById(gameID);
        if(!foundSingleGame.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Cannot find the game belong to the user", "")
            );

        SingleGame singleGame = foundSingleGame.get();
        singleGame.setMoves(singleGame.getMoves() + Long.toString(playerMove.getMove()));
        singleGameRepository.save(singleGame);

        return ResponseEntity.status(HttpStatus.OK).body(
                new Response("ok", "This move is successfully sent", "")
        );
    }
}
