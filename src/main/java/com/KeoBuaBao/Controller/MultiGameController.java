package com.KeoBuaBao.Controller;

import com.KeoBuaBao.Entity.*;

import com.KeoBuaBao.HelperClass.*;
import com.KeoBuaBao.Responses.*;
import com.KeoBuaBao.Repository.MultiGameRepository;
import com.KeoBuaBao.Repository.PlayerMultiGameRepository;
import com.KeoBuaBao.Repository.RoomRepository;
import com.KeoBuaBao.Repository.UserRepository;
import com.KeoBuaBao.Utility.DateUtilis;
import com.KeoBuaBao.Utility.DetermineResult;
import com.KeoBuaBao.Utility.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/multiplayer")
public class MultiGameController {
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
    public ResponseEntity<Response> createMutiplayer(@PathVariable Long roomID, @RequestBody User user) {
        // Check null token
        if(user.getToken() == null) return Errors.NotImplemented("Token cannot be null");

        // Check null datetime
        if(user.getStatus() == null)
            return Errors.NotImplemented("Datetime cannot be null");

        if (user.getUsername() == null) return Errors.NotFound("user");

        List<User> foundUser = userRepository.findByUsername(user.getUsername());
        if (foundUser.isEmpty()) return Errors.NotFound("user");

        User currentUser = foundUser.get(0);
        // Check equal token
        if (DateUtilis.isTokenExpired(currentUser.getStatus(), user.getStatus())) return Errors.Expired("token");
        String serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), user.getStatus());
        if(!serverToken.equals(user.getToken()))
            return Errors.NotImplemented("Tokens do not match");
        currentUser.setStatus(DateUtilis.getCurrentDate());

        Optional<Room> foundRoom = roomRepository.findById(roomID);
        if(!foundRoom.isPresent()) return Errors.NotFound("room");

        Room currentRoom = foundRoom.get();
        if(currentRoom.getPlayerOne() == null || currentRoom.getPlayerTwo() == null) {
            return Errors.NotImplemented("Not enough players to start");
        }

        var foundHost = userRepository.findByUsername(currentRoom.getHost());
        User host = foundHost.get(0);

        MultiGame multiGame = new MultiGame();
        multiGame.setTimePerMove(host.getTimePerMove());
        multiGame.setNumberRounds(host.getNumberRound());

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

        return Success.WithData("Create game successfully", multiGame);
    }

    // API to get the player from a match
    @GetMapping("/{gameID}")
    public ResponseEntity<Response> getPlayerFromMatch(@PathVariable Long gameID, @RequestBody User user) {
        // Check null token
        if(user.getToken() == null)
            return Errors.NotImplemented("Token cannot be null");

        // Check null datetime
        if(user.getStatus() == null)
            return Errors.NotImplemented("Datetime cannot be null");

        if (user.getUsername() == null) return Errors.NotFound("user");

        List<User> foundUser = userRepository.findByUsername(user.getUsername());
        if (foundUser.isEmpty()) return Errors.NotFound("user");

        User currentUser0 = foundUser.get(0);
        // Check equal token
        if (DateUtilis.isTokenExpired(currentUser0.getStatus(), user.getStatus())) return Errors.Expired("token");
        String serverToken = SecurityUtils.generateToken(currentUser0.getUsername(), currentUser0.getPassword(), user.getStatus());
        if(!serverToken.equals(user.getToken()))
            return Errors.NotImplemented("Tokens do not match");
        currentUser0.setStatus(DateUtilis.getCurrentDate());

        Optional<MultiGame> foundMultiGame = multiGameRepository.findById(gameID);
        if(!foundMultiGame.isPresent()) return Errors.NotFound("game");

        MultiGame currentMultigame = foundMultiGame.get();
        var playerMultiGameList = currentMultigame.getPlayerMultiGame();
        var usernameList = new ArrayList<String>();

        for (PlayerMultiGame playerMultiGame : playerMultiGameList) {
            User currentUser = playerMultiGame.getUser();
            String username = currentUser.getUsername();
            usernameList.add(username);
        }

        return Success.WithData("Here is the players belong to the match", usernameList);
    }


    @PostMapping("/playMultiplayer/{gameID}")
    public ResponseEntity<Response> playOnline(@PathVariable long gameID, @RequestBody MultiplayerMove playerMove) {
        // Check null token
        if(playerMove.getToken() == null)
            return Errors.NotImplemented("Token cannot be null");

        // Check null datetime
        if(playerMove.getStatus() == null)
            return Errors.NotImplemented("Datetime cannot be null");

        if (playerMove.getUsername() == null) return Errors.NotFound("user");

        List<User> foundUser = userRepository.findByUsername(playerMove.getUsername());
        if (foundUser.isEmpty()) return Errors.NotFound("user");

        User currentUser0 = foundUser.get(0);
        // Check equal token
        if (DateUtilis.isTokenExpired(currentUser0.getStatus(), playerMove.getStatus())) return Errors.Expired("token");
        String serverToken = SecurityUtils.generateToken(currentUser0.getUsername(), currentUser0.getPassword(), playerMove.getStatus());
        if(!serverToken.equals(playerMove.getToken()))
            return Errors.NotImplemented("Tokens do not match");
        currentUser0.setStatus(DateUtilis.getCurrentDate());

        Optional<MultiGame> foundMultiGame = multiGameRepository.findById(gameID);
        if(!foundMultiGame.isPresent()) return Errors.NotFound("game");

        if(playerMove.getMove() < 1 || playerMove.getMove() > 3) {
            return Errors.NotImplemented("Illegal move");
        }

        MultiGame currentMultigame = foundMultiGame.get();
        if(currentMultigame.getResultOne().length() >= currentMultigame.getNumberRounds()) {
            return Errors.NotImplemented("The game is over!");
        }


        var playerMultiGameList = currentMultigame.getPlayerMultiGame();
        var usernameList = new ArrayList<String>();

        for (PlayerMultiGame playerMultiGame : playerMultiGameList) {
            User currentUser = playerMultiGame.getUser();
            String username = currentUser.getUsername();
            usernameList.add(username);
        }

        if(!usernameList.contains(playerMove.getUsername())) return Errors.NotImplemented("You are not in the game");

        int playerPosition;
        if(playerMove.getUsername().equals(usernameList.get(0))) playerPosition = 0;
        else playerPosition = 1;


        var currentPlayerMultiGame = playerMultiGameList.get(playerPosition);
        var opponentPlayerMultiGame = playerMultiGameList.get(1 - playerPosition);
        int currentPlayer_MoveNumber = currentPlayerMultiGame.getMoves().length();
        int opponentPlayer_MoveNumber = opponentPlayerMultiGame.getMoves().length();

        if (currentPlayer_MoveNumber > opponentPlayer_MoveNumber) {
            return Errors.NotImplemented("Please wait for the opponent to make a move");
        }

        currentPlayerMultiGame.setMoves(currentPlayerMultiGame.getMoves() + playerMove.getMove());
        PlayerMultiGameRepository.save(currentPlayerMultiGame);

        if (currentPlayer_MoveNumber + 1 > opponentPlayer_MoveNumber) {
            return Success.NoData("Send move success! Please wait for the opponent to make a move");
        }
        else {
            String player1moves = playerMultiGameList.get(0).getMoves();
            String player2moves = playerMultiGameList.get(1).getMoves();
            String latestPlayerOneMove = Character.toString(player1moves.charAt(player1moves.length() - 1));
            String latestPlayerTwoMove = Character.toString(player2moves.charAt(player2moves.length() - 1));

            var resultList = DetermineResult.announceResult(Integer.parseInt(latestPlayerOneMove), Integer.parseInt(latestPlayerTwoMove));
            currentMultigame.setResultOne(currentMultigame.getResultOne() + resultList.get(0));
            currentMultigame.setResultTwo(currentMultigame.getResultTwo() + resultList.get(1));
            multiGameRepository.save(currentMultigame);
            return Success.NoData("Send move success! Please wait for the round to end");
        }
    }

    @PostMapping("/get_round_result")
    public ResponseEntity<Response> getRoundResultMultiplayer(@RequestBody GameIDAndUsername user) {

        // Check null token
        if(user.getToken() == null)
            return Errors.NotImplemented("Token cannot be null");

        // Check null datetime
        if(user.getStatus() == null)
            return Errors.NotImplemented("Datetime cannot be null");

        if (user.getUsername() == null) return Errors.NotFound("user");

        List<User> foundUser = userRepository.findByUsername(user.getUsername());
        if (foundUser.isEmpty()) return Errors.NotFound("user");

        User currentUser = foundUser.get(0);
        // Check equal token
        if (DateUtilis.isTokenExpired(currentUser.getStatus(), user.getStatus())) return Errors.Expired("token");
        String serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), user.getStatus());
        if(!serverToken.equals(user.getToken()))
            return Errors.NotImplemented("Tokens do not match");
        currentUser.setStatus(DateUtilis.getCurrentDate());

        Optional<MultiGame> foundMultigame = multiGameRepository.findById(user.getGameID());
        if (!foundMultigame.isPresent()) return Errors.NotFound("game");

        int playerPosition = -1;

        MultiGame currentMultigame = foundMultigame.get();
        var playerMultiGameList = currentMultigame.getPlayerMultiGame();
        for (int i = 0; i < playerMultiGameList.size(); i++) {
            User tempUser = playerMultiGameList.get(i).getUser();
            if (tempUser.getUsername().equals(currentUser.getUsername()))
                playerPosition = i;
        }

        if (playerPosition == -1) return Errors.NotImplemented("You are not in the game");

        String result;
        if (playerPosition == 0) result = currentMultigame.getResultOne();
        else result = currentMultigame.getResultTwo();

        if (result.charAt(result.length() - 1) == '+')
            return Success.NoData("Congratulation! You have won this round!");

        else if (result.charAt(result.length() - 1) == '-')
            return Success.NoData("Unfortunately, the opponent has beaten you!");

        else return Success.NoData("The game results in a draw!");
    }
}
