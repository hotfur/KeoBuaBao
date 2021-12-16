package com.KeoBuaBao.Controller;

import com.KeoBuaBao.Entity.Room;
import com.KeoBuaBao.Entity.User;
import com.KeoBuaBao.HelperClass.JoinRoom;
import com.KeoBuaBao.HelperClass.Response;
import com.KeoBuaBao.Repository.RoomRepository;
import com.KeoBuaBao.Repository.UserRepository;
import com.KeoBuaBao.Utility.ConvertListtoString;
import com.KeoBuaBao.Utility.LinearSearch;
import com.KeoBuaBao.Utility.PlayersListUtilis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/room")
public class RoomController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;

    // Get all available rooms
    @GetMapping("")
    public List<Room> getAllAvailableRoom() {
        return roomRepository.findAll();
    }

    @PostMapping("/create_room")
    public ResponseEntity<Response> createNewRoom(@RequestBody User username) {
        if (username.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Username cannot be empty", "")
            );
        }

        List<User> foundUsername = userRepository.findByUsername(username.getUsername());
        if (foundUsername.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Cannot find the user", "")
            );
        }

        User currentUser = foundUsername.get(0);

        // Check whether the player is already in a room
        if(currentUser.getRoomId() != null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "You are already in a room", "")
            );
        }

        Room room = new Room();
        room.setPlayers("");
        room.setHost(username.getUsername());
        roomRepository.save(room);

        currentUser.setRoomId(room.getId());
        userRepository.save(currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(
                new Response("ok", "New room is created", room)
        );
    }

    // API Quit room
    @PostMapping("/quit_room")
    public ResponseEntity<Response> quitRoom(@RequestBody User username) {
        if (username.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Username cannot be empty", "")
            );
        }

        List<User> foundUsername = userRepository.findByUsername(username.getUsername());
        if (foundUsername.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Cannot find the user", "")
            );
        }

        User currentUser = foundUsername.get(0);

        if(currentUser.getRoomId() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "No room to out", "")
            );
        }

        Optional<Room> foundRoom = roomRepository.findById(currentUser.getRoomId());
        if(!foundRoom.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Cannot find the room belong to this user", "")
            );
        }

        Room currentRoom = foundRoom.get();
        String allPlayers = currentRoom.getPlayers();
        List<String> allPlayersList = PlayersListUtilis.getAllPlayers(allPlayers);
        // Check if this player is the host
        if(username.getUsername().equals(currentRoom.getHost())) {
            if(allPlayersList.isEmpty()) {
                roomRepository.deleteById(currentUser.getRoomId());

                currentUser.setRoomId(null);
                userRepository.save(currentUser);
            }
            else {
                currentRoom.setHost(allPlayersList.get(allPlayersList.size() - 1));
                allPlayersList.remove(allPlayersList.size() - 1);
                currentRoom.setPlayers(ConvertListtoString.convertToString(allPlayersList));
                roomRepository.save(currentRoom);

                currentUser.setRoomId(null);
                userRepository.save(currentUser);
            }
        }

        else {
            int foundIndex = LinearSearch.linearFind(allPlayersList, username.getUsername());
            allPlayersList.remove(foundIndex);
            currentRoom.setPlayers(ConvertListtoString.convertToString(allPlayersList));
            roomRepository.save(currentRoom);

            currentUser.setRoomId(null);
            userRepository.save(currentUser);

        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new Response("ok", "You have been out of the room", "")
        );
    }

    @PostMapping("/join_room")
    public ResponseEntity<Response> joinRoom(@RequestBody JoinRoom newPlayer) {
        if (newPlayer.getUsername() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Username cannot be empty", "")
            );
        }

        if(newPlayer.getRoomID() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "The room id cannot be empty", "")
            );
        }

        List<User> foundUsername = userRepository.findByUsername(newPlayer.getUsername());
        if (foundUsername.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Cannot find the user", "")
            );
        }

        Optional<Room> foundRoom = roomRepository.findById(newPlayer.getRoomID());
        if(!foundRoom.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "Cannot find the room", "")
            );
        }

        User currentUser = foundUsername.get(0);

        if(currentUser.getRoomId() != null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Response("fail", "You are already in a room", "")
            );
        }

        Room currentRoom = foundRoom.get();
        currentRoom.setPlayers(currentRoom.getPlayers() + newPlayer.getUsername() + " ");
        roomRepository.save(currentRoom);

        currentUser.setRoomId(currentRoom.getId());
        userRepository.save(currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(
                new Response("ok", "Join the room successfully", "")
        );
    }
}
