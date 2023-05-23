package cinema.repository;

import cinema.data.CinemaRoom;
import cinema.data.Order;
import cinema.data.dto.CinemaQueryDTO;
import cinema.data.dto.RevokeQueryDTO;
import cinema.exception.NotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class CinemaRepository {
    private final Map<String, CinemaRoom> cinemaRooms = new HashMap<>();
    private final Map<String, Order> orders = new HashMap<>();

    @PostConstruct
    public void initCinemaRooms() {
        CinemaRoom kidsRoom = new CinemaRoom("kids", 5, 5, Map.of(0, 59));
        cinemaRooms.put(kidsRoom.getName(), kidsRoom);
        CinemaRoom overallRoom = new CinemaRoom("overall", 30, 40, Map.of(0, 129, 10, 159, 20, 199));
        cinemaRooms.put(overallRoom.getName(), overallRoom);
        CinemaRoom matureRoom = new CinemaRoom("mature", 20, 30, Map.of(0, 209, 20, 359));
        cinemaRooms.put(matureRoom.getName(), matureRoom);
    }

    public CinemaRoom getCinemaRoomByName(String name){
        return cinemaRooms.get(name);
    }

    public CinemaQueryDTO getCinemaData(CinemaQueryDTO query){
        CinemaRoom cinemaRoom = cinemaRooms.get(query.getRoomName());
        if (cinemaRoom == null)
            throw new NotFoundException("Room with name "+ query.getRoomName() +" not found!");
        query.setData(cinemaRoom);
        return query;
    }

    public Map<String, Order> getOrders() {
        return orders;
    }

    public void registerOrder(Order order) {
        orders.put(order.getToken(), order);
    }

    public void removeOrder(String token) {
        orders.remove(token);
    }

    public RevokeQueryDTO revokeOrder(RevokeQueryDTO revokeQuery) {
        if (revokeQuery.getToken() == null)
            throw new NotFoundException("Token is missing");
        Order order = orders.get(revokeQuery.getToken());
        if (order == null)
            throw new NotFoundException("Order "+ revokeQuery.getToken() +" not found or already revoked!");
        CinemaRoom cinemaRoom = getCinemaRoomByName(order.getRoomName());
        cinemaRoom.revokeOrder(order);
        removeOrder(order.getToken());
        revokeQuery.injectData(order);
        return revokeQuery;
    }
}
