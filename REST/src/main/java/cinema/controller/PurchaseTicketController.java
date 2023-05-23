package cinema.controller;

import cinema.data.Order;
import cinema.data.dto.PurchaseQueryDTO;
import cinema.service.PurchaseTicketService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PurchaseTicketController {
    private PurchaseTicketService purchaseTicketService;

    @PostMapping(value = "/order", produces = MediaType.APPLICATION_JSON_VALUE)
    public Order processPurchase(@RequestBody PurchaseQueryDTO purchase) {
        return purchaseTicketService.processPurchase(purchase);
    }
}
