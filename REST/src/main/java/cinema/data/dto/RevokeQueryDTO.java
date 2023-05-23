package cinema.data.dto;

import cinema.data.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class RevokeQueryDTO {
    private String roomName;
    private String token;
    private int moneyReturned;
    private int seatsReturned;
    private Map<Integer, int[]> returnedSeats;

    public void injectData(Order order) {
        roomName = order.getRoomName();
        moneyReturned = order.getOrderPrice();
        seatsReturned = order.getSeatsAmount();
        returnedSeats = order.getSeatsData();
    }
}
