package cinema.repository;

import cinema.data.CinemaRoom;
import cinema.data.Order;
import cinema.data.dto.CinemaQueryDTO;
import cinema.data.dto.RevokeQueryDTO;
import cinema.exception.NotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class CinemaRepository {
    private final Map<String, CinemaRoom> cinemaRooms = new HashMap<>();
    private final CinemaOrders orders = new CinemaOrders();

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

    public CinemaOrders getOrders() {
        return orders;
    }

    public void registerOrder(Order order){
        orders.registerOrder(order);
    }

    public Order removeOrder(String token){
        return orders.removeOrder(token);
    }

    @Getter
    public static class CinemaOrders {
        private final Map<String, Map<String, Order>> orders = new HashMap<>();

        public void registerOrder(Order order) {
            Map<String, Order> ordersInRoom = orders.computeIfAbsent(order.getRoomName(), k -> new HashMap<>());
            ordersInRoom.put(order.getToken(), order);
        }

        public Order removeOrder(String token) {
            String roomNameFromToken = token.split(":", 2)[0];
            Map<String, Order> ordersInRoom = orders.get(roomNameFromToken);
            if (ordersInRoom == null || !ordersInRoom.containsKey(token)) {
                return null;
            }
            Order order = ordersInRoom.get(token);
            ordersInRoom.remove(token);
            return order;
        }
    }
}
