package cinema.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

enum OrderStatus {
    FAIL,
    SEMI_SUCCESS,
    SUCCESS,
}

@Getter
public class Order {
    OrderStatus status = OrderStatus.FAIL;
    String token = "";
    int price = 0;
    int seatsAmount = 0;
    Map<Integer, int[]> seatsData;

    Order(){ }

    Order(OrderStatus status, int price, int seatsAmount, Map<Integer, int[]> seatsData){
        this.status = status;
        this.price = price;
        this.seatsAmount = seatsAmount;
        this.seatsData = seatsData;
        token = UUID.randomUUID().toString();
    }
}
