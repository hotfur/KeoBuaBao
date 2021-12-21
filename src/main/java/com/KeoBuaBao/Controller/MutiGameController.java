package com.KeoBuaBao.Controller;

import com.KeoBuaBao.Entity.MultiGame;
import com.KeoBuaBao.Entity.PlayerMultiGame;
import com.KeoBuaBao.Entity.Room;
import com.KeoBuaBao.Entity.User;
import com.KeoBuaBao.HelperClass.Response;
import com.KeoBuaBao.Repository.MultiGameRepository;
import com.KeoBuaBao.Repository.PlayerMultiGameRepository;
import com.KeoBuaBao.Repository.RoomRepository;
import com.KeoBuaBao.Repository.UserRepository;
import com.KeoBuaBao.Utility.DateUtilis;
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

        List<User> foundHost = userRepository.findByUsername(currentRoom.getHost());
        User host = foundHost.get(0);

        MultiGame multiGame = new MultiGame();
        multiGame.setDateTime(DateUtilis.getCurrentDate());
        multiGame.setTimePerMove(host.getTimePerMove());
        multiGame.setNumberRounds(host.getNumberRound());
        multiGame.setResultOne("");
        multiGame.setResultTwo("");

        User Player1 = userRepository.findByUsername(currentRoom.getPlayerOne()).get(0);
        User Player2 = userRepository.findByUsername(currentRoom.getPlayerTwo()).get(0);
        PlayerMultiGame playerMultiGame_1 = new PlayerMultiGame();
        playerMultiGame_1.setMultiGame(multiGame);
        playerMultiGame_1.setUser(Player1);
        PlayerMultiGame playerMultiGame_2 = new PlayerMultiGame();
        playerMultiGame_2.setMultiGame(multiGame);
        playerMultiGame_2.setUser(Player2);
        List<PlayerMultiGame> PlayerList = new ArrayList<PlayerMultiGame>();
        PlayerList.add(playerMultiGame_1);
        PlayerList.add(playerMultiGame_2);
        multiGame.setPlayerMultiGame(PlayerList);

        List<PlayerMultiGame> PlayerList1 = Player1.getPlayerMultiGame();
        PlayerList1.add(playerMultiGame_1);
        Player1.setPlayerMultiGame(PlayerList1);

        List<PlayerMultiGame> PlayerList2 = Player2.getPlayerMultiGame();
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
}
