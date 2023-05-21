package cinema.data;

import java.util.HashMap;
import java.util.Map;

public class SeatRow {
    private final int price;
    private final Map<Integer, Boolean> seats = new HashMap<>();

    public SeatRow(int price, int seatsAmount) {
        this.price = price;
        for (int i = 1; i <= seatsAmount; i++) {
            seats.put(i, true);
        }
    }

    public Map<Integer, Boolean> getSeats() {
        return seats;
    }

    public int getSeatPrice() {
        return price;
    }

    public boolean tryBookSeat(int seatId) {
        if (seats.get(seatId)) {
            seats.put(seatId, false);
            return true;
        }
        return false;
    }

    public void revokeSeat(int seatId) {
        seats.put(seatId, true);
    }
}
