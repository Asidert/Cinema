package cinema.data;

import cinema.exceptions.NotEnoughArgumentsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
public class CinemaRoom {
    @JsonIgnore
    ObjectMapper mapper = new ObjectMapper();
    @JsonIgnore
    private final int totalRows;
    @JsonIgnore
    private final int totalColumns;
    private final Map<Integer, SeatRow> seatRows = new HashMap<>();
    @JsonIgnore
    private final Map<String, Map<Integer, int[]>> orders = new HashMap<>();
    private final int maximumSeatsCount;
    private int availableSeatsCount;
    @JsonIgnore
    private int income = 0;

    public CinemaRoom(int rows, int columns, Map<Integer, Integer> priceRanges) {
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

    private void registerOrder(Order order) {
        if (order.getStatus() == OrderStatus.FAIL)
            return;
        orders.put(order.getToken(), order.getSeatsData());
    }

    public String processSimpleOrder(int row, int column){
        SeatRow seatRow = seatRows.get(row);
        Order order;
        if (seatRow.tryBookSeat(column)) {
            order = new Order(OrderStatus.SUCCESS, seatRow.getSeatPrice(), 1, Map.of(row, new int[]{column}));
        } else {
            order = new Order();
        }
        registerOrder(order);
        try {
            return mapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public String processOrder(JsonNode orderData){
        Map<Integer, int[]> orderMap;
        try {
            orderMap = mapper.convertValue(orderData, new TypeReference<>() {});
        } catch (Exception e) {
            throw new NotEnoughArgumentsException();
        }
        int orderPrice = 0;
        int orderedSeats = 0;
        boolean withoutCollisions = true;
        for (int key: orderMap.keySet()) {
            SeatRow row = seatRows.get(key);
            int[] ordersInRow = orderMap.get(key);
            for (int i = ordersInRow.length - 1; i >= 0; i--) {
                if (row.tryBookSeat(ordersInRow[i])) {
                    orderPrice += row.getSeatPrice();
                    orderedSeats++;
                    availableSeatsCount--;
                } else {
                    withoutCollisions = false;
                    ordersInRow[i] = -1;
                }
            }
        }
        income += orderPrice;
        OrderStatus status;
        if (orderPrice == 0) {
            status = OrderStatus.FAIL;
        } else {
            status = withoutCollisions ? OrderStatus.SUCCESS : OrderStatus.SEMI_SUCCESS;
        }
        Order order = new Order(status, orderPrice, orderedSeats, orderMap);
        registerOrder(order);
        try {
            return mapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public boolean revokeOrder(String token) {
        if (!orders.containsKey(token))
            return false;
        processRevokeOrder(token);
        return true;
    }

    private void processRevokeOrder(String token) {
        Map<Integer, int[]> orderData = orders.get(token);
        for (int key: orderData.keySet()) {
            SeatRow row = seatRows.get(key);
            int[] ordersInRow = orderData.get(key);
            for (int seat: ordersInRow) {
                if (seat == -1) {
                    continue;
                }
                row.revokeSeat(seat);
                availableSeatsCount++;
            }
        }
        orders.remove(token);
    }

    @JsonIgnore
    public Map<String, Map<Integer, int[]>> getOrders() {
        return orders;
    }

    @JsonIgnore
    public String getStats() {
        return String.format("{\"current_income\":%d,\"maximum_seats\":%d,\"available_seats\":%d}", income, maximumSeatsCount, availableSeatsCount);
    }
}