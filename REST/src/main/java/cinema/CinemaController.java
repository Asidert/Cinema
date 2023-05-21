package cinema;

import cinema.data.CinemaRoom;
import cinema.data.Seat;

import cinema.exception.NotEnoughArgumentsException;
import cinema.exception.OutOfRoomBoundsException;
import cinema.exception.SeatAlreadyBookedException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.UUID;
import java.util.Optional;

import java.util.Map;

@RestController
public class CinemaController {
    CinemaRoom cinemaRoom = new CinemaRoom();
    @GetMapping("/seats")
    public CinemaRoom getAvailableSeats() {
        return cinemaRoom;
    }

    @PostMapping(value = "/purchase", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> purchaseSeat(@RequestBody JsonNode body) {
        if (!body.has("row") || !body.has("column")){
            throw new NotEnoughArgumentsException();
        }
        int row = body.get("row").asInt();
        int column = body.get("column").asInt();
        if (row < 0 || row > cinemaRoom.getTotalRows() || column < 0 || column > cinemaRoom.getTotalColumns()) {
            //throw new OutOfRoomBoundsException();
            return new ResponseEntity<>("{\"error\":\"The number of a row or a column is out of bounds!\"}", HttpStatus.BAD_REQUEST);
        }
        String token = cinemaRoom.tryBookSeat(row, column);
        if (token == null) {
            //throw new SeatAlreadyBookedException();
            return new ResponseEntity<>("{\"error\":\"The ticket has been already purchased!\"}", HttpStatus.BAD_REQUEST);
        }
        int price = row <= 4 ? 10 : 8;
        return new ResponseEntity<>(String.format("{\"token\":\"%s\",\"ticket\":{\"row\":%d,\"column\":%d,\"price\":%d}}", token, row, column, price), HttpStatus.OK);
    }

    @PostMapping("/return")
    public ResponseEntity<String> buySeats(@RequestBody JsonNode body) {
        if (!body.has("token")){
            throw new NotEnoughArgumentsException();
        }
        String token = body.get("token").textValue();
        Seat seat = cinemaRoom.tryReturnTicket(token);
        if (seat == null) {
            return new ResponseEntity<>("{\"error\":\"Wrong token!\"}", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(String.format("{\"returned_ticket\":{\"row\":%d,\"column\":%d,\"price\":%d}}", seat.getRow(), seat.getColumn(), seat.getPrice()), HttpStatus.OK);
    }

    @PostMapping("/stats")
    public ResponseEntity<String> getStats(@RequestParam Optional<String> password) {
        if (!password.isPresent()){
            return new ResponseEntity<>("{\"error\":\"The password is wrong!\"}", HttpStatus.UNAUTHORIZED);
        }
        if (!(password.get().equals("super_secret"))) {
            return new ResponseEntity<>("{\"error\":\"The password is wrong!\"}", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(cinemaRoom.getStats(), HttpStatus.OK);
    }
}