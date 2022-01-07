package com.KeoBuaBao.Controller;

import com.KeoBuaBao.Entity.*;

import com.KeoBuaBao.Responses.*;
import com.KeoBuaBao.Repository.MultiGameRepository;
import com.KeoBuaBao.Repository.PlayerMultiGameRepository;
import com.KeoBuaBao.Repository.RoomRepository;
import com.KeoBuaBao.Repository.UserRepository;
import com.KeoBuaBao.Utility.DateUtilis;
import com.KeoBuaBao.Utility.DetermineResult;
import com.KeoBuaBao.Utility.SecurityUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class controls the multiplayer games
 */
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

    @Getter @Setter @NoArgsConstructor
    private class GameResult {
        private String player1;
        private String player2;
        private long round;
    }
    /**
     * Perform username, datetime, token checks against user provided data to authenticate
     * @param user the user undergoing authentication
     * @return ResponseEntity if fail any check, User entity if pass all checks
     */
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
        // Check for deleted account
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
     * For admin use only
     * @return all multiplayer games in the database
     */
    @GetMapping("")
    public List<MultiGame> getAllMultiplayerGame() {
        return multiGameRepository.findAll();
    }

    // Start game API: Create a row in the table multiplayer

    /**
     * Start a multiplayer game with two players in position 1 and 2. Both must be ready for the game to begin.
     * @param user either player one or two can start the game.
     * @return errors if failed. success status to indicate the game has been started.
     */
    @PostMapping("/start")
    public ResponseEntity<Response> createMultiplayer(@RequestBody User user) {
        // Perform some basic user checking
        Object check = userCheck(user);
        if (check instanceof ResponseEntity) return (ResponseEntity<Response>) check;
        User currentUser = ((User) check);

        Room currentRoom = currentUser.getRoom();
        if(currentRoom == null) return Errors.NotFound("room");
        
        // In order to start the game, it should consist of fully two players

        if(currentRoom.getPlayerOne() == null || currentRoom.getPlayerTwo() == null)
            return Errors.NotImplemented("Not enough players to start");

        // Get the two player of the game
        User Player1 = userRepository.findByUsername(currentRoom.getPlayerOne()).get(0);
        User Player2 = userRepository.findByUsername(currentRoom.getPlayerTwo()).get(0);

        // Check if the game have been started
        if (currentRoom.getGame() != null) return Errors.NotImplemented("The game has started!");

        //Check if the current user is indeed player 1 or 2, otherwise refuse to start the game
        if(Player1.getUsername().equals(user.getUsername())) {
            if (currentRoom.isPlayerOneReady()) {
                currentRoom.setPlayerOneReady(false);
                roomRepository.save(currentRoom);
                return Success.NoData("You have unready!");
            }
            else {
                currentRoom.setPlayerOneReady(true);
                roomRepository.save(currentRoom);
                if (!currentRoom.isPlayerTwoReady())
                    return Success.NoData("Ready! Please wait for your opponent to start the game");
            }
        }
        else if (Player2.getUsername().equals(user.getUsername())) {
            if (currentRoom.isPlayerTwoReady()) {
                currentRoom.setPlayerTwoReady(false);
                roomRepository.save(currentRoom);
                return Success.NoData("You have unready!");
            }
            else {
                currentRoom.setPlayerTwoReady(true);
                roomRepository.save(currentRoom);
                if (!currentRoom.isPlayerOneReady())
                    return Success.NoData("Ready! Please wait for your opponent to start the game");
            }
        }
        else {
            return Errors.NotImplemented("You are not authorized to start this game");
        }

        // Find the host of the room
        User host = userRepository.findByUsername(currentRoom.getHost()).get(0);
        
        // Create a multiplayer game record to save in the database
        MultiGame multiGame = new MultiGame();
        multiGame.setHost(currentRoom.getHost());
        multiGame.setPlayer1(currentRoom.getPlayerOne());
        multiGame.setPlayer2(currentRoom.getPlayerTwo());
        multiGame.setTimePerMove(host.getTimePerMove());
        multiGame.setNumberRounds(host.getNumberRound());

        // Set the record for player one and player two. In other words, add data to the list in MultiGame entity.
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

        // Save all records to the necessary entities.
        PlayerMultiGameRepository.save(playerMultiGame_1);
        PlayerMultiGameRepository.save(playerMultiGame_2);
        multiGameRepository.save(multiGame);
        userRepository.save(Player1);
        userRepository.save(Player2);

        currentRoom.setGame(multiGame);
        roomRepository.save(currentRoom);

        return Success.WithData("Create game successfully", multiGame);
    }

    /**
     * Allow player to make a move in a multiplayer game
     * @param user an entity contains the player authentication and move information
     * @return errors if failed. Otherwise, a response that acknowledges the move.
     */
    @PostMapping("/play")
    public ResponseEntity<Response> playOnline(@RequestBody User user) {
        //Check for illegal moves
        if(user.getMove() < 1 || user.getMove() > 3) {
            return Errors.NotImplemented("Illegal move");
        }

        // Perform some basic user checking
        Object check = userCheck(user);
        if (check instanceof ResponseEntity) return (ResponseEntity<Response>) check;
        User currentUser = ((User) check);

        Room currentRoom = currentUser.getRoom();
        if (currentRoom == null) return Errors.NotImplemented("You are not in a room");
        MultiGame currentMultigame = currentRoom.getGame();
        if (currentMultigame == null) return Errors.NotImplemented("No game is being played at the moment");


        int playerPosition;
        if (user.getUsername().equals(currentRoom.getPlayerOne())) playerPosition = 0;
        else if (user.getUsername().equals(currentRoom.getPlayerTwo())) playerPosition = 1;
        else return Errors.NotImplemented("You are not a player");

        var playerMultiGameList = currentMultigame.getPlayerMultiGame();
        var currentPlayerMultiGame = playerMultiGameList.get(playerPosition);
        var opponentPlayerMultiGame = playerMultiGameList.get(1 - playerPosition);
        int currentPlayer_MoveNumber = currentPlayerMultiGame.getMoves().length();
        int opponentPlayer_MoveNumber = opponentPlayerMultiGame.getMoves().length();

        if (currentPlayer_MoveNumber > opponentPlayer_MoveNumber)
            return Errors.NotImplemented("Move already sent! Please wait for the round to end");

        currentPlayerMultiGame.setMoves(currentPlayerMultiGame.getMoves() + user.getMove());
        PlayerMultiGameRepository.save(currentPlayerMultiGame);

        if (currentPlayer_MoveNumber + 1 <= opponentPlayer_MoveNumber) {
            String player1moves = playerMultiGameList.get(0).getMoves();
            String player2moves = playerMultiGameList.get(1).getMoves();
            String latestPlayerOneMove = Character.toString(player1moves.charAt(player1moves.length() - 1));
            String latestPlayerTwoMove = Character.toString(player2moves.charAt(player2moves.length() - 1));

            var resultList = DetermineResult.announceResult(Integer.parseInt(latestPlayerOneMove), Integer.parseInt(latestPlayerTwoMove));
            currentMultigame.setResultOne(currentMultigame.getResultOne() + resultList.get(0));
            currentMultigame.setResultTwo(currentMultigame.getResultTwo() + resultList.get(1));
            multiGameRepository.save(currentMultigame);

            // If this is the ending move, then reset the ready counter and disassociate the current game
            // with this room, allowing players to play more games.

            //We have to update the final result of the match into user database too
            if (currentMultigame.getResultOne().length() >= currentMultigame.getNumberRounds()) {
                currentRoom.setPlayerOneReady(false);
                currentRoom.setPlayerTwoReady(false);
                currentRoom.setGame(null);
                roomRepository.save(currentRoom);

                User player1 = playerMultiGameList.get(0).getUser();
                User player2 = playerMultiGameList.get(1).getUser();
                long countWin = 0;
                long countLose = 0;
                String result = currentMultigame.getResultOne();
                for(int i = 0; i < result.length(); i++) {
                    if(result.charAt(i) == '+')
                        countWin++;
                    else if(result.charAt(i) == '-')
                        countLose++;
                }

                if (countWin > countLose) {
                    player1.setWin(player1.getWin()+1);
                    player2.setLoss(player2.getLoss()+1);
                }
                else if (countLose > countWin) {
                    player2.setWin(player2.getWin()+1);
                    player1.setLoss(player1.getLoss()+1);
                }
                else {
                    player1.setTie(player1.getTie()+1);
                    player2.setTie(player2.getTie()+1);
                }
                userRepository.save(player1);
                userRepository.save(player2);
            }
        }
        return Success.NoData("Send move success! Please wait for the round to end");
    }

    /**
     * FLAGGED!!!
     * Return the result of the nearest move to the player.
     * @param user the player who just made a move.
     * @return the result of the previous move. errors if failed.
     */
    @PostMapping("/get_round_result")
    public ResponseEntity<Response> getRoundResultMultiplayer(@RequestBody User user) {
        // Perform some basic user checking
        Object check = userCheck(user);
        if (check instanceof ResponseEntity) return (ResponseEntity<Response>) check;
        User currentUser = ((User) check);

        Room currentRoom = currentUser.getRoom();
        if (currentRoom == null) return Errors.NotImplemented("You are not in a room");
        MultiGame currentMultigame = currentRoom.getGame();
        if (currentMultigame == null) return Errors.NotImplemented("No game is being played at the moment");

        if (currentMultigame.getResultOne().length() >= currentMultigame.getNumberRounds()) {
            return Errors.NotImplemented("The game is over!");
        }

        var playerMultiGameList = currentMultigame.getPlayerMultiGame();
        var player1Moves = playerMultiGameList.get(0).getMoves();
        var player2Moves = playerMultiGameList.get(1).getMoves();
        String resultOne = currentMultigame.getResultOne();

        int playerPosition = -1;
        if (user.getUsername().equals(currentRoom.getPlayerOne())) playerPosition = 0;
        else if (user.getUsername().equals(currentRoom.getPlayerTwo())) playerPosition = 1;

        // Get the round result
        if (resultOne.isEmpty()) return Success.NoData("First round has not finished");
        char roundResultOne = resultOne.charAt(resultOne.length() - 1);
        GameResult gameResult = new GameResult();
        gameResult.setRound(currentMultigame.getNumberRounds());
        gameResult.setPlayer1(player1Moves.substring(player1Moves.length()-1));
        gameResult.setPlayer2(player2Moves.substring(player2Moves.length()-1));
        if (playerPosition != -1) {
            if (player1Moves.length() == player2Moves.length()) {
                String result;
                if (playerPosition == 0) result = currentMultigame.getResultOne();
                else result = currentMultigame.getResultTwo();


                if (result.charAt(result.length() - 1) == '+')
                    return Success.WithData("Congratulation! You have won this round!", gameResult);

                else if (result.charAt(result.length() - 1) == '-')
                    return Success.WithData("Unfortunately, the opponent has beaten you!", gameResult);

                else return Success.WithData("The game results in a draw!", gameResult);
            }
            else {
                return Errors.NotImplemented("Please wait for the round to finish");
            }
        }
        // Case 1: When player one wins
        if (roundResultOne == '+') return Success.WithData("So far player one wins this round", gameResult);
        // Case 2: When player two wins
        else if (roundResultOne == '-') return Success.WithData("So far player two wins this round", gameResult);
        // Case 3: When the game draws
        else return Success.WithData("So far this round ends up with a tie", gameResult);
    }

    @PostMapping("/get_current_game")
    public ResponseEntity<Response> getAllMultiplayerGame(@RequestBody User user) {
        // Perform some basic user checking
        Object check = userCheck(user);
        if (check instanceof ResponseEntity) return (ResponseEntity<Response>) check;
        User currentUser = ((User) check);

        Room currentRoom = currentUser.getRoom();
        if(currentRoom == null)
            return Errors.NotFound("Room");
        // Have to actually fetch the object from the db before jackson serialization
        MultiGame currentGame = Hibernate.unproxy(currentRoom.getGame(), MultiGame.class);
        return Success.WithData("Here is the game the room is playing", currentGame);
    }

    /**
     * Get all multiplayer game of a specified user
     * @param user the user wants to see
     * @return all multiplayer games related to that user
     */
    @PostMapping("/get_player_games")
    public ResponseEntity<Response> getOnePlayerMultiGame(@RequestBody User user) {
        // Perform some basic user checking
        Object check = userCheck(user);
        if (check instanceof ResponseEntity) return (ResponseEntity<Response>) check;
        User currentUser = ((User) check);

        // Get the list of all single game in the database
        List<PlayerMultiGame> foundPlayerMultiGame = currentUser.getPlayerMultiGame();
        if (!foundPlayerMultiGame.isEmpty()) {
            // Fetch them from database
            List<MultiGame> foundMultiGame = new ArrayList<>();
            for (PlayerMultiGame playerMultiGame : foundPlayerMultiGame) {
                foundMultiGame.add(Hibernate.unproxy(playerMultiGame.getMultiGame(), MultiGame.class));
            }
            return Success.WithData("Here is all of the game from the user" , foundMultiGame);
        }
        else return Errors.NotFound("games");
    }
}
