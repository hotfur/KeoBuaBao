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

/**
 * Implement Room Controller for the Room entity. Basically, this controller class will consist of several APIs for
 * the room function
 *
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @author Nguyen Van Trang
 */
@RestController
@RequestMapping("/room")
public class RoomController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;

    /**
     * Return a response entity error if a unique object (username, password, etc) is empty
     * @param object a string representation for the field
     * @return a response entity with the "cannot be empty" message
     */
    private static ResponseEntity<Response> EmptyError(String object) {
        return Errors.NotImplemented(object + " cannot be empty");
    }

    /**
     * An API allows the user to get all available rooms to join
     * @return a list consisting of all active rooms
     */
    @GetMapping("")
    public List<Room> getAllAvailableRoom() {
        // Add token check to this method
        return roomRepository.findAll();
    }

    /**
     * An API to allow the user to create a new room to play. Initially, the server should check the user's token from
     * the client. If both token resemble, the server will change the user information. Note that one player can only
     * create or join one room at one time.
     *
     * @param user the user information, including token, status, and username
     * @return a response entity demonstrates whether the user creates a room successfully or not
     */
    @PostMapping("/create_room")
    public ResponseEntity<Response> createNewRoom(@RequestBody User user) {
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
        if(foundUser.isEmpty())
            return Errors.NotFound("user");

        User currentUser = foundUser.get(0); // Get the corresponding user record
        // Check equal token
        if (DateUtilis.isTokenExpired(currentUser.getStatus(), user.getStatus())) return Errors.Expired("token");
        String serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), user.getStatus());
        // Remember not implement the request when the tokens do not match
        if(!serverToken.equals(user.getToken()))
            return Errors.NotImplemented("Tokens do not match");
        currentUser.setStatus(DateUtilis.getCurrentDate());

        // Check whether the player is already in a room
        if(currentUser.getRoom() != null) return Errors.NotImplemented("You are already in a room");

        // Create a new room record to store
        Room room = new Room();
        room.setPlayers("");
        room.setHost(currentUser.getUsername()); // Since the player creates the room, he or she will initially be the host.
        roomRepository.save(room);

        currentUser.setRoom(room);
        userRepository.save(currentUser);

        return Success.WithData("New room is created", room); // Everything is successful, room can be created
    }

    /**
     * Removes user from a room he is currently in. User has to be authenticated.
     * @param user User profile of the player who want to leave the room
     * @return errors if failed. success status to indicate the user successfully left the room.
     */
    @PostMapping("/quit_room")
    public ResponseEntity<Response> quitRoom(@RequestBody User user) {
        // Check null token
        if(user.getToken() == null)
            return Errors.NotImplemented("Token cannot be null");

        // Check null datetime
        if(user.getStatus() == null)
            return Errors.NotImplemented("Datetime cannot be null");

        if (user.getUsername() == null)
            return Errors.NotFound("user");

        // Find the corresponding user information with the given username.
        List<User> foundUser = userRepository.findByUsername(user.getUsername());
        // Catch an error when the user cannot be found
        if (foundUser.isEmpty())
            return Errors.NotFound("user");

        User currentUser = foundUser.get(0);
        // Check equal token or whether token is expired from the most recent login attempt.
        if (DateUtilis.isTokenExpired(currentUser.getStatus(), user.getStatus()))
            return Errors.Expired("token");
        String serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), user.getStatus());
        if(!serverToken.equals(user.getToken()))
            return Errors.NotImplemented("Tokens do not match");
        currentUser.setStatus(DateUtilis.getCurrentDate());

        Room currentRoom = currentUser.getRoom();

        // Catch the error when the room is not found
        if(currentRoom == null) return Errors.NotFound("the room belong to this user");

        String allPlayers = currentRoom.getPlayers();
        // Get the list of all members in the room
        var allPlayersList = PlayersListUtilis.getAllPlayers(allPlayers);

        // Another case is that the user can either in player seat one or two, so make sure to delete (make null) to
        // the seat appropriately too.
        if(currentRoom.getPlayerOne() != null && currentRoom.getPlayerOne().equals(user.getUsername()))
            currentRoom.setPlayerOne(null);

        if(currentRoom.getPlayerTwo() != null && currentRoom.getPlayerTwo().equals(user.getUsername()))
            currentRoom.setPlayerTwo(null);

        roomRepository.save(currentRoom); // Update the room information entity when done

        // Check if this player is the host
        if(user.getUsername().equals(currentRoom.getHost())) {
            // Deal with the case when the user is the last member to quit the room. With that, the server will delete
            // the room record from the database too as well as update the user's room status to null, meaning the user
            // does not belong to any rooms.
            if (allPlayersList.isEmpty()) {
                roomRepository.delete(currentRoom);
                currentUser.setRoom(null);
                userRepository.save(currentUser);
            }
            // However, when there are several users in the room, we should deal with the case differently. First, we
            // will randomly assign the host. In this case, we will assign to the last member because remove the last
            // element from the list takes O(1) time complexity, which is cheap to do. After deletion, update the
            // room's participant list and the user's room ID information.
            else {
                currentRoom.setHost(allPlayersList.get(allPlayersList.size() - 1));
                allPlayersList.remove(allPlayersList.size() - 1);
                currentRoom.setPlayers(ConvertListtoString.convertToString(allPlayersList));
                roomRepository.save(currentRoom);

                currentUser.setRoom(null);
                userRepository.save(currentUser);
            }
        }

        // If the player is not the host. We will need to find the players from the list linearly. Although linear
        // search takes O(n) to perform, we think that it is acceptable in this case. Then, we will remove that player
        // and update the database as well as the user's room ID information.
        else {
            int foundIndex = LinearSearch.linearFind(allPlayersList, user.getUsername());
            allPlayersList.remove(foundIndex);
            currentRoom.setPlayers(ConvertListtoString.convertToString(allPlayersList));
            roomRepository.save(currentRoom);

            currentUser.setRoom(null);
            userRepository.save(currentUser);
        }

        return Success.NoData("You have been out of the room"); // Notify a successful message without data needed.
    }

    /**
     * Add a user to a room as a spectator. Requires authentication.
     * @param newPlayer User who wishes to join a room
     * @return errors if failed. success status to indicate the user successfully enter the room.
     */
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

        if(currentUser.getRoom() != null) return Errors.NotImplemented("You are already in a room");

        Room currentRoom = foundRoom.get();
        currentRoom.setPlayers(currentRoom.getPlayers() + newPlayer.getUsername() + " ");
        roomRepository.save(currentRoom);

        currentUser.setRoom(currentRoom);
        userRepository.save(currentUser);

        return Success.NoData("Join the room successfully");
    }

    /**
     * Makes a spectator becomes a player in the room.
     * @param newPlayer the spectator in the room who wishes to become a player
     * @return errors if failed. success status to indicate the user successfully became player.
     */
    @PostMapping("/enter_play_seat")
    public ResponseEntity<Response> joinPlaySeat(@RequestBody EnterGame newPlayer) {
        if (newPlayer.getUsername() == null) return EmptyError("Username");

        // Check null token
        if(newPlayer.getToken() == null)
            return Errors.NotImplemented("Token cannot be null");

        // Check null datetime
        if(newPlayer.getStatus() == null)
            return Errors.NotImplemented("Datetime cannot be null");

        // Find the
        List<User> foundUser = userRepository.findByUsername(newPlayer.getUsername());
        if (foundUser.isEmpty())
            return Errors.NotFound("user");

        User currentUser = foundUser.get(0);
        // Check equal token
        if (DateUtilis.isTokenExpired(currentUser.getStatus(), newPlayer.getStatus())) return Errors.Expired("token");
        String serverToken = SecurityUtils.generateToken(currentUser.getUsername(), currentUser.getPassword(), newPlayer.getStatus());
        if(!serverToken.equals(newPlayer.getToken()))
            return Errors.NotImplemented("Tokens do not match");
        currentUser.setStatus(DateUtilis.getCurrentDate());

        Room currentRoom = currentUser.getRoom();
        if(currentUser.getRoom() == null)
            return Errors.NotImplemented("You are not belonged to any rooms");

        if(newPlayer.getPlayerPosition() != 1 && newPlayer.getPlayerPosition() != 2)
            return Errors.NotImplemented("Invalid seat, only seat number one or two");

        if(currentRoom.getPlayerOne() != null && currentRoom.getPlayerOne().equals(newPlayer.getUsername()))
            return Errors.NotImplemented("You are already in seat one");

        if(currentRoom.getPlayerTwo() != null && currentRoom.getPlayerTwo().equals(newPlayer.getUsername()))
            return Errors.NotImplemented("You are already in seat two");

        if(newPlayer.getPlayerPosition() == 1) {
            if(currentRoom.getPlayerOne() == null)
                currentRoom.setPlayerOne(newPlayer.getUsername());
            else
                return Errors.NotImplemented("Seat is already occupied");
        }

        else {
            if(currentRoom.getPlayerTwo() == null)
                currentRoom.setPlayerTwo(newPlayer.getUsername());
            else return Errors.NotImplemented("Seat is already occupied");
        }

        roomRepository.save(currentRoom);
        return Success.NoData("You are now player " + newPlayer.getPlayerPosition());
    }

    /**
     * Quit a player seat and become a spectator.
     * @param user the user who wishes to quit the player seat and become a spectator instead.
     * @return errors if failed. success status to indicate the user successfully left the player seat.
     */
    @PostMapping("/quit_play_seat")
    public ResponseEntity<Response> quitPlaySeat(@RequestBody User user) {
        // Check null username
        if (user.getUsername() == null)
            return EmptyError("Username");

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

        Room currentRoom = currentUser.getRoom();
        if(currentUser.getRoom() == null)
            return Errors.NotImplemented("You are not belonged to any rooms");

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
