package cinema.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

enum OrderStatus {
    FAIL,
    SEMI_SUCCESS,
    SUCCESS,
}

@Getter
@NoArgsConstructor
public class Order {
    OrderStatus status = OrderStatus.FAIL;
    String token;
    String roomName;
    int orderPrice = 0;
    int seatsAmount = 0;
    Map<Integer, int[]> seatsData;

    public Order(OrderStatus status, String roomName, int orderPrice, int seatsAmount, Map<Integer, int[]> seatsData){
        this.status = status;
        this.roomName = roomName;
        this.orderPrice = orderPrice;
        this.seatsAmount = seatsAmount;
        this.seatsData = seatsData;
        token = roomName + ":" + UUID.randomUUID().toString();
    }
}
