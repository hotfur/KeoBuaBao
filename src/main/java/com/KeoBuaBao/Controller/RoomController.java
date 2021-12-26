package com.KeoBuaBao.Controller;

import com.KeoBuaBao.Entity.Room;
import com.KeoBuaBao.Entity.User;
import com.KeoBuaBao.HelperClass.*;
import com.KeoBuaBao.Repository.RoomRepository;
import com.KeoBuaBao.Repository.UserRepository;
import com.KeoBuaBao.Responses.*;
import com.KeoBuaBao.Utility.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    //Show error for empty object
    private static ResponseEntity<Response> EmptyError(String object) {
        return Errors.NotImplemented(object + " cannot be empty");
    }

    // Get all available rooms
    @GetMapping("")
    public List<Room> getAllAvailableRoom() {
        return roomRepository.findAll();
    }

    @PostMapping("/create_room")
    public ResponseEntity<Response> createNewRoom(@RequestBody User user) {
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

        // Check whether the player is already in a room
        if(currentUser.getRoomId() != null) {
            return Errors.NotImplemented("You are already in a room");
        }

        Room room = new Room();
        room.setPlayers("");
        room.setHost(user.getUsername());
        roomRepository.save(room);

        currentUser.setRoomId(room.getId());
        userRepository.save(currentUser);

        return Success.WithData("New room is created", room);
    }

    // API Quit room
    @PostMapping("/quit_room")
    public ResponseEntity<Response> quitRoom(@RequestBody User user) {
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

        if(currentUser.getRoomId() == null) return Errors.NotImplemented("You are not in any room");

        Optional<Room> foundRoom = roomRepository.findById(currentUser.getRoomId());
        if(!foundRoom.isPresent()) return Errors.NotFound("the room belong to this user");

        Room currentRoom = foundRoom.get();
        String allPlayers = currentRoom.getPlayers();
        var allPlayersList = PlayersListUtilis.getAllPlayers(allPlayers);
        // Check if this player is the host
        if(user.getUsername().equals(currentRoom.getHost())) {
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
            int foundIndex = LinearSearch.linearFind(allPlayersList, user.getUsername());
            allPlayersList.remove(foundIndex);
            currentRoom.setPlayers(ConvertListtoString.convertToString(allPlayersList));
            roomRepository.save(currentRoom);

            currentUser.setRoomId(null);
            userRepository.save(currentUser);
        }

        if(currentRoom.getPlayerOne() != null && currentRoom.getPlayerOne().equals(user.getUsername()))
            currentRoom.setPlayerOne(null);

        if(currentRoom.getPlayerTwo() != null && currentRoom.getPlayerTwo().equals(user.getUsername()))
            currentRoom.setPlayerTwo(null);

        roomRepository.save(currentRoom);
        return Success.NoData("You have been out of the room");
    }

    @PostMapping("/join_room")
    public ResponseEntity<Response> joinRoom(@RequestBody JoinRoom newPlayer) {
        if (newPlayer.getUsername() == null) return EmptyError("Username");
        if(newPlayer.getRoomID() == null) return EmptyError("Room id");

        // Check null token
        if(newPlayer.getToken() == null)
            return Errors.NotImplemented("Token cannot be null");

        // Check null datetime
        if(newPlayer.getStatus() == null)
            return Errors.NotImplemented("Datetime cannot be null");

        List<User> foundUser = userRepository.findByUsername(newPlayer.getUsername());
        if (foundUser.isEmpty()) return Errors.NotFound("user");

        User currentUser = foundUser.get(0);
        // Check equal token
        if (DateUtilis.isTokenExpired(currentUser.getStatus(), newPlayer.getStatus())) return Errors.Expired("token");
        String serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), newPlayer.getStatus());
        if(!serverToken.equals(newPlayer.getToken()))
            return Errors.NotImplemented("Tokens do not match");
        currentUser.setStatus(DateUtilis.getCurrentDate());

        Optional<Room> foundRoom = roomRepository.findById(newPlayer.getRoomID());
        if(!foundRoom.isPresent()) return Errors.NotFound("room");

        if(currentUser.getRoomId() != null) return Errors.NotImplemented("You are already in a room");

        Room currentRoom = foundRoom.get();
        currentRoom.setPlayers(currentRoom.getPlayers() + newPlayer.getUsername() + " ");
        roomRepository.save(currentRoom);

        currentUser.setRoomId(currentRoom.getId());
        userRepository.save(currentUser);

        return Success.NoData("Join the room successfully");
    }

    // Enter a game seat API
    @PostMapping("/enter_play_seat")
    public ResponseEntity<Response> joinPlaySeat(@RequestBody EnterGame newPlayer) {
        if (newPlayer.getUsername() == null) return EmptyError("Username");

        // Check null token
        if(newPlayer.getToken() == null)
            return Errors.NotImplemented("Token cannot be null");

        // Check null datetime
        if(newPlayer.getStatus() == null)
            return Errors.NotImplemented("Datetime cannot be null");

        List<User> foundUser = userRepository.findByUsername(newPlayer.getUsername());
        if (foundUser.isEmpty()) return Errors.NotFound("user");

        User currentUser = foundUser.get(0);
        // Check equal token
        if (DateUtilis.isTokenExpired(currentUser.getStatus(), newPlayer.getStatus())) return Errors.Expired("token");
        String serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), newPlayer.getStatus());
        if(!serverToken.equals(newPlayer.getToken()))
            return Errors.NotImplemented("Tokens do not match");
        currentUser.setStatus(DateUtilis.getCurrentDate());

        Optional<Room> foundRoom = roomRepository.findById(currentUser.getRoomId());
        if(!foundRoom.isPresent()) return Errors.NotFound("room");

        Room currentRoom = foundRoom.get();

        if(newPlayer.getPlayerPosition() != 1 && newPlayer.getPlayerPosition() != 2) {
            return Errors.NotImplemented("Invalid seat, only seat number one or two");
        }

        if(currentRoom.getPlayerOne() != null && currentRoom.getPlayerOne().equals(newPlayer.getUsername())) {
            return Errors.NotImplemented("You are already in seat one");
        }

        if(currentRoom.getPlayerTwo() != null && currentRoom.getPlayerTwo().equals(newPlayer.getUsername())) {
            return Errors.NotImplemented("You are already in seat two");
        }

        if(newPlayer.getPlayerPosition() == 1) {
            if(currentRoom.getPlayerOne() == null)
                currentRoom.setPlayerOne(newPlayer.getUsername());
            else return Errors.NotImplemented("Seat is already occupied");
        }

        else {
            if(currentRoom.getPlayerTwo() == null)
                currentRoom.setPlayerTwo(newPlayer.getUsername());
            else return Errors.NotImplemented("Seat is already occupied");
        }

        roomRepository.save(currentRoom);
        return Success.NoData("You are now player " + newPlayer.getPlayerPosition());
    }

    // Enter a game seat API
    @PostMapping("/quit_play_seat")
    public ResponseEntity<Response> quitPlaySeat(@RequestBody User user) {
        if (user.getUsername() == null) return EmptyError("Username");

        // Check null token
        if(user.getToken() == null)
            return Errors.NotImplemented("Token cannot be null");

        // Check null datetime
        if(user.getStatus() == null)
            return Errors.NotImplemented("Datetime cannot be null");

        List<User> foundUser = userRepository.findByUsername(user.getUsername());
        if (foundUser.isEmpty()) return Errors.NotFound("user");

        User currentUser = foundUser.get(0);
        // Check equal token
        if (DateUtilis.isTokenExpired(currentUser.getStatus(), user.getStatus())) return Errors.Expired("token");
        String serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), user.getStatus());
        if(!serverToken.equals(user.getToken()))
            return Errors.NotImplemented("Tokens do not match");
        currentUser.setStatus(DateUtilis.getCurrentDate());

        if(currentUser.getRoomId() == null)
            return Errors.NotImplemented("You are not belonged to any rooms");

        Optional<Room> foundRoom = roomRepository.findById(currentUser.getRoomId());
        if(!foundRoom.isPresent()) return Errors.NotFound("room");

        Room currentRoom = foundRoom.get();

        // The player is in seat one
        if(user.getUsername().equals(currentRoom.getPlayerOne()))
            currentRoom.setPlayerOne(null);

        else if(user.getUsername().equals(currentRoom.getPlayerTwo()))
            currentRoom.setPlayerTwo(null);

        else return Errors.NotImplemented("You do not belong to any seats");
        roomRepository.save(currentRoom);
        return Success.NoData("You are now out of the seat");
    }
}
