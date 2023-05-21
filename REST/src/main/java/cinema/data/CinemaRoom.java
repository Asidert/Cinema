package cinema.data;

import lombok.Getter;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;

@Getter
public class CinemaRoom {
    private final int totalRows = 9;
    private final int totalColumns = 9;
    //private final int premiumSeatPrice = 10;
    //private final int commonSeatPrice = 8;

    private final List<Seat> availableSeats = new ArrayList<>();
    //private final List<Seat> bookedSeats = new ArrayList<>();

    @JsonIgnore
    private Map<String, Seat> buyedTickets = new HashMap<>();

    @JsonIgnore
    private List<String> returnedTickets = new ArrayList<>();

    @JsonIgnore
    private int income = 0;

    @JsonIgnore
    private int availableSeatsCount = totalRows * totalColumns;

    @JsonIgnore
    private int purchasedTicketsCount = 0;

    public CinemaRoom() {
        for (int i = 1; i <= this.totalRows; i++) {
            for (int j = 1; j <= this.totalRows; j++) {
                availableSeats.add(new Seat(i, j, i <= 4 ? 10 : 8));
            }
        }
    }

    public Seat getRequestedSeat(int row, int column){
        for (Seat seat : availableSeats) {
            if (seat.getRow() == row && seat.getColumn() == column)
                return seat;
        }
        return null;
    }

    public String tryBookSeat(int row, int column){
        Seat seat = getRequestedSeat(row, column);
        if (seat == null) {
            return null;
        }
        availableSeats.remove(seat);
        income += seat.getPrice();
        availableSeatsCount--;
        purchasedTicketsCount++;
        //bookedSeats.add(seat);
        String uuid = UUID.randomUUID().toString();
        registerTicket(uuid, seat);
        return uuid;
    }

    private void registerTicket(String token, Seat seat){
        buyedTickets.put(token, seat);
    }

    public Seat tryReturnTicket(String token){
        if (!buyedTickets.containsKey(token)) {
            return null;
        }
        Seat seat = buyedTickets.get(token);
        availableSeats.add(seat);
        income -= seat.getPrice();
        availableSeatsCount++;
        purchasedTicketsCount--;
        buyedTickets.remove(token);
        returnedTickets.add(token);
        return seat;
    }

    @JsonIgnore
    public String getStats() {
        return String.format("{\"current_income\":%d,\"number_of_available_seats\":%d,\"number_of_purchased_tickets\":%d}", income, availableSeatsCount, purchasedTicketsCount);
    }
}