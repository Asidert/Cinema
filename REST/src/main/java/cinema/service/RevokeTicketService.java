package cinema.service;

import cinema.data.CinemaRoom;
import cinema.data.Order;
import cinema.data.dto.RevokeQueryDTO;
import cinema.exception.NotFoundException;
import cinema.repository.CinemaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RevokeTicketService {
    private CinemaRepository cinemaRepository;

    public RevokeQueryDTO revokeOrder(RevokeQueryDTO revokeQuery) {
        if (revokeQuery.getToken() == null)
            throw new NotFoundException("Token is missing");
        Order order = cinemaRepository.getOrders().get(revokeQuery.getToken());
        if (order == null)
            throw new NotFoundException("Order "+ revokeQuery.getToken() +" not found or already revoked!");
        CinemaRoom cinemaRoom = cinemaRepository.getCinemaRoomByName(order.getRoomName());
        cinemaRoom.revokeOrder(order);
        cinemaRepository.removeOrder(order.getToken());
        revokeQuery.injectData(order);
        return revokeQuery;
    }
}