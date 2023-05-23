package cinema.data.dto;

import cinema.data.CinemaRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CinemaQueryDTO {
    private String roomName;
    private CinemaRoom data;
}
