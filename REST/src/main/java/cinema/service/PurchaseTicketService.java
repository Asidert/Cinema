package cinema.service;

import cinema.data.*;
import cinema.data.dto.PurchaseQueryDTO;
import cinema.exception.NotFoundException;
import cinema.repository.CinemaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PurchaseTicketService {
    private CinemaRepository cinemaRepository;

    public Order processPurchase(PurchaseQueryDTO purchase) {
        CinemaRoom cinemaRoom = cinemaRepository.getCinemaRoomByName(purchase.getRoomName());
        if (cinemaRoom == null)
            throw new NotFoundException("Room with name "+ purchase.getRoomName() +" not found!");
        Order order = cinemaRoom.processOrder(purchase);
        cinemaRepository.registerOrder(order);
        return order;
    }
}