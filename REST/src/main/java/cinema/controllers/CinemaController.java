package cinema.controllers;

import cinema.data.CinemaRoom;

import cinema.exceptions.NotEnoughArgumentsException;
import cinema.exceptions.RoomNotExistException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CinemaController {
    private final Map<String, CinemaRoom> cinemaRooms = new HashMap<>();

    @PostConstruct
    public void InitCinemaRooms() {
        CinemaRoom kidsRoom = new CinemaRoom(5, 5, Map.of(0, 59));
        cinemaRooms.put("kids_room", kidsRoom);
        CinemaRoom overallRoom = new CinemaRoom(30, 40, Map.of(0, 129, 10, 159, 20, 199));
        cinemaRooms.put("overall_room", overallRoom);
        CinemaRoom matureRoom = new CinemaRoom(20, 30, Map.of(0, 209, 20, 359));
        cinemaRooms.put("mature_room", matureRoom);
    }

    @GetMapping("/seats/{roomName}")
    public CinemaRoom getAvailableSeats(@PathVariable String roomName) {
        if (!cinemaRooms.containsKey(roomName)) {
            return null;
        }
        return cinemaRooms.get(roomName);
    }

    @PostMapping(value = "/order/{roomName}/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> purchaseSeat(@PathVariable String roomName, @PathVariable String type, @RequestBody JsonNode body) {
        if (!cinemaRooms.containsKey(roomName)) {
            throw new RoomNotExistException();
        }
        switch (type) {
            case "seat" -> {
                if (!body.has("row") || !body.has("column")) {
                    throw new NotEnoughArgumentsException();
                }
                int row = body.get("row").asInt();
                int column = body.get("column").asInt();
                String simpleOrderData = cinemaRooms.get(roomName).processSimpleOrder(row, column);
                return new ResponseEntity<>(simpleOrderData, HttpStatus.OK);
            }
            case "multiple" -> {
                String orderData = cinemaRooms.get(roomName).processOrder(body);
                return new ResponseEntity<>(orderData, HttpStatus.OK);
            }
            case "revoke" -> {
                if (!body.has("token")) {
                    throw new NotEnoughArgumentsException();
                }
                if (cinemaRooms.get(roomName).revokeOrder(body.get("token").textValue())) {
                    return new ResponseEntity<>("{\"revoke\": true}", HttpStatus.OK);
                }
            }
        }
        throw new NotEnoughArgumentsException();
    }

    @PostMapping("/orders/{roomName}")
    public Map<String, Map<Integer, int[]>> getStats(@PathVariable String roomName) {
        if (!cinemaRooms.containsKey(roomName)) {
            throw new RoomNotExistException();
        }
        return cinemaRooms.get(roomName).getOrders();
    }
}
