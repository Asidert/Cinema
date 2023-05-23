package cinema.data;

import cinema.data.dto.PurchaseQueryDTO;
import cinema.exception.NotFoundException;
import cinema.repository.CinemaRepository;
import lombok.Getter;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
public class CinemaRoom {
    private final String name;
    @JsonIgnore
    private final int totalRows;
    @JsonIgnore
    private final int totalColumns;
    private final Map<Integer, SeatRow> seatRows = new HashMap<>();
    private final int maximumSeatsCount;
    private int availableSeatsCount;
    @JsonIgnore
    private int income = 0;

    public CinemaRoom(String name, int rows, int columns, Map<Integer, Integer> priceRanges) {
        this.name = name;
        totalRows = rows;
        totalColumns = columns;
        maximumSeatsCount = rows * columns;
        availableSeatsCount = rows * columns;
        int currentRowPrice = priceRanges.get(0);
        for (int i = 1; i <= totalRows; i++) {
            Integer priceOfRow = priceRanges.get(i);
            if (priceOfRow != null) {
                currentRowPrice = priceOfRow;
            }
            seatRows.put(i, new SeatRow(currentRowPrice, totalColumns));
        }
    }

    public SeatRow getSeatRowById(int id) {
        SeatRow seatRow = seatRows.get(id);
        if (seatRow == null)
            throw new NotFoundException("There are no seats row with id " + id + " in room with name "+ getName());
        return seatRow;
    }

    public Order processOrder(PurchaseQueryDTO purchase){
        int processedPrice = 0;
        int processedSeatsCount = 0;
        boolean hasCollisions = false;
        Map<Integer, int[]> orderedSeats = purchase.getOrderedSeats();
        for (int key: orderedSeats.keySet()) {
            SeatRow row = getSeatRowById(key);
            int[] ordersInRow = orderedSeats.get(key);
            for (int i = ordersInRow.length - 1; i >= 0; i--) {
                if (row.tryBookSeat(ordersInRow[i])) {
                    processedPrice += row.getSeatPrice();
                    processedSeatsCount++;
                } else {
                    hasCollisions = true;
                    ordersInRow[i] = -1;
                }
            }
        }
        return registerOrder(orderedSeats, processedPrice, processedSeatsCount, hasCollisions);
    }

    private Order registerOrder(Map<Integer, int[]> orderedSeats, int income, int seats, boolean hasCollisions) {
        if (income == 0) {
            return new Order();
        }
        this.income += income;
        availableSeatsCount -= seats;
        return new Order(hasCollisions ? OrderStatus.SEMI_SUCCESS : OrderStatus.SUCCESS, getName(), income, seats, orderedSeats);
    }

    public void revokeOrder(Order order) {
        Map<Integer, int[]> orderData = order.getSeatsData();
        for (int key: orderData.keySet()) {
            SeatRow row = seatRows.get(key);
            int[] ordersInRow = orderData.get(key);
            for (int seat : ordersInRow) {
                if (seat == -1) {
                    continue;
                }
                row.revokeSeat(seat);
                income -= row.getSeatPrice();
                availableSeatsCount++;
            }
        }
    }
}