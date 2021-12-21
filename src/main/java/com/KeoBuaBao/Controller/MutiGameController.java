package com.KeoBuaBao.Controller;

import com.KeoBuaBao.Entity.*;

import com.KeoBuaBao.HelperClass.MultiplayerMove;
import com.KeoBuaBao.HelperClass.Response;
import com.KeoBuaBao.Repository.MultiGameRepository;
import com.KeoBuaBao.Repository.PlayerMultiGameRepository;
import com.KeoBuaBao.Repository.RoomRepository;
import com.KeoBuaBao.Repository.UserRepository;
import com.KeoBuaBao.Utility.DateUtilis;
import com.KeoBuaBao.Utility.DetermineResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/multiplayer")
public class MutiGameController {
    @Autowired
    private MultiGameRepository multiGameRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlayerMultiGameRepository PlayerMultiGameRepository;

    @GetMapping("")
    public List<MultiGame> getAllMultiplayerGame() {
        return multiGameRepository.findAll();
    }

    // Start game API: Create a row in the table multiplayer
    @PostMapping("/{roomID}")
    public ResponseEntity<Response> createMutiplayer(@PathVariable Long roomID) {
        Optional<Room> foundRoom = roomRepository.findById(roomID);
        if(!foundRoom.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Cannot find the room", "")
            );
        }

        Room currentRoom = foundRoom.get();
        if(currentRoom.getPlayerOne() == null || currentRoom.getPlayerTwo() == null) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new Response("fail", "Not enough players to start", "")
            );
        }

        var foundHost = userRepository.findByUsername(currentRoom.getHost());
        User host = foundHost.get(0);

        MultiGame multiGame = new MultiGame();
        multiGame.setDateTime(DateUtilis.getCurrentDate());
        multiGame.setTimePerMove(host.getTimePerMove());
        multiGame.setNumberRounds(host.getNumberRound());
        multiGame.setResultOne("");
        multiGame.setResultTwo("");

        User Player1 = userRepository.findByUsername(currentRoom.getPlayerOne()).get(0);
        User Player2 = userRepository.findByUsername(currentRoom.getPlayerTwo()).get(0);
        var playerMultiGame_1 = new PlayerMultiGame();
        playerMultiGame_1.setMultiGame(multiGame);
        playerMultiGame_1.setUser(Player1);
        var playerMultiGame_2 = new PlayerMultiGame();
        playerMultiGame_2.setMultiGame(multiGame);
        playerMultiGame_2.setUser(Player2);
        var PlayerList = new ArrayList<PlayerMultiGame>();
        PlayerList.add(playerMultiGame_1);
        PlayerList.add(playerMultiGame_2);
        multiGame.setPlayerMultiGame(PlayerList);

        var PlayerList1 = Player1.getPlayerMultiGame();
        PlayerList1.add(playerMultiGame_1);
        Player1.setPlayerMultiGame(PlayerList1);

        var PlayerList2 = Player2.getPlayerMultiGame();
        PlayerList2.add(playerMultiGame_2);
        Player2.setPlayerMultiGame(PlayerList2);

        PlayerMultiGameRepository.save(playerMultiGame_1);
        PlayerMultiGameRepository.save(playerMultiGame_2);
        multiGameRepository.save(multiGame);
        userRepository.save(Player1);
        userRepository.save(Player2);

        return ResponseEntity.status(HttpStatus.OK).body(
                    new Response("ok", "Create game successfully" , multiGame)
        );
    }

    // API to get the player from a match
    @GetMapping("/{gameID}")
    public ResponseEntity<Response> getPlayerFromMatch(@PathVariable Long gameID) {
        Optional<MultiGame> foundMultiGame = multiGameRepository.findById(gameID);
        if(!foundMultiGame.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Cannot found the game", "")
            );
        }

        MultiGame currentMultigame = foundMultiGame.get();
        var playerMultiGameList = currentMultigame.getPlayerMultiGame();
        var usernameList = new ArrayList<String>();

        for(int i = 0; i < playerMultiGameList.size(); i++) {
            User currentUser = playerMultiGameList.get(i).getUser();
            String username = currentUser.getUsername();
            usernameList.add(username);
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new Response("ok", "Here is the players belong to the match" , usernameList)
        );
    }








    @PostMapping("/playMultiplayer/{gameID}")
    public ResponseEntity<Response> playOnline(@PathVariable long gameID, @RequestBody MultiplayerMove playerMove) {
        Optional<MultiGame> foundMultiGame = multiGameRepository.findById(gameID);
        if(!foundMultiGame.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Cannot found the game", "")
            );
        }

        if(playerMove.getMove() < 1 || playerMove.getMove() > 3) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new Response("fail", "Illegal move", "")
            );
        }

        MultiGame currentMultigame = foundMultiGame.get();
        if(currentMultigame.getResultOne().length() >= currentMultigame.getNumberRounds()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("done", "Game over!", "")
            );
        }


        var playerMultiGameList = currentMultigame.getPlayerMultiGame();
        var usernameList = new ArrayList<String>();

        for(int i = 0; i < playerMultiGameList.size(); i++) {
            User currentUser = playerMultiGameList.get(i).getUser();
            String username = currentUser.getUsername();
            usernameList.add(username);
        }

        if(usernameList.contains(playerMove.getUsername())) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new Response("fail", "You are not in the game", "")
            );
        }

        int playerPosition = -1;
        if(playerMove.getUsername().equals(usernameList.get(0))) {
            playerPosition = 0;
        }
        else {
            playerPosition = 1;
        }


        var currentPlayerMultiGame = playerMultiGameList.get(playerPosition);
        var opponentPlayerMultiGame = playerMultiGameList.get(1 - playerPosition);
        int currentPlayer_MoveNumber = currentPlayerMultiGame.getMoves().length();
        int opponentPlayer_MoveNumber = opponentPlayerMultiGame.getMoves().length();

        if (currentPlayer_MoveNumber > opponentPlayer_MoveNumber) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new Response("fail", "Please wait for the opponent to make a move", "")
            );
        }

        currentPlayerMultiGame.setMoves(currentPlayerMultiGame.getMoves() + playerMove.getMove());
        PlayerMultiGameRepository.save(currentPlayerMultiGame);

        if (currentPlayer_MoveNumber + 1 > opponentPlayer_MoveNumber) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new Response("OK", "Send move success! Please wait for the opponent to make a move", "")
            );
        }
        else {
            String player1moves = playerMultiGameList.get(0).getMoves();
            String player2moves = playerMultiGameList.get(1).getMoves();
            var resultList = DetermineResult.announceResult(player1moves.charAt(player1moves.length() - 1), player2moves.charAt(player2moves.length() - 1));
            currentMultigame.setResultOne(currentMultigame.getResultOne() + resultList.get(0));
            currentMultigame.setResultTwo(currentMultigame.getResultTwo() + resultList.get(1));
            multiGameRepository.save(currentMultigame);
            String returnString;
            if (resultList.get(playerPosition).equals("+")) returnString = "Congratulation! You have won this round!";
            else if (resultList.get(playerPosition).equals("-")) returnString = "Unfortunately, the opponent has beaten you!";
            else returnString = "The game results in a draw!";
            return ResponseEntity.status(HttpStatus.OK).body(
                    new Response("OK", returnString, opponentPlayerMultiGame.getMoves().charAt(opponentPlayer_MoveNumber - 1))
            );
        }
    }


}
