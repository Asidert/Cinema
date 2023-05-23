package cinema.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseQueryDTO {
    private String roomName;
    private Map<Integer, int[]> orderedSeats;
}
