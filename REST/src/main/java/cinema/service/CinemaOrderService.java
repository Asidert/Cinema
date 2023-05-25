package cinema.service;

import cinema.data.*;
import cinema.data.dto.PurchaseQueryDTO;
import cinema.data.dto.RevokeQueryDTO;
import cinema.exception.NotFoundException;
import cinema.repository.CinemaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CinemaOrderService {
    private CinemaRepository cinemaRepository;

    public Order processPurchase(PurchaseQueryDTO purchase) {
        CinemaRoom cinemaRoom = cinemaRepository.getCinemaRoomByName(purchase.getRoomName());
        if (cinemaRoom == null)
            throw new NotFoundException("Room with name " + purchase.getRoomName() + " not found!");
        Order order = cinemaRoom.processOrder(purchase);
        cinemaRepository.registerOrder(order);
        return order;
    }

    public RevokeQueryDTO revokeOrder(RevokeQueryDTO revokeQuery) {
        if (revokeQuery.getToken() == null)
            throw new NotFoundException("Token is missing");
        Order order = cinemaRepository.removeOrder(revokeQuery.getToken());
        if (order == null)
            throw new NotFoundException("Order " + revokeQuery.getToken() + " not found or already revoked!");
        CinemaRoom cinemaRoom = cinemaRepository.getCinemaRoomByName(order.getRoomName());
        cinemaRoom.revokeOrder(order);
        revokeQuery.injectData(order);
        return revokeQuery;
    }
}