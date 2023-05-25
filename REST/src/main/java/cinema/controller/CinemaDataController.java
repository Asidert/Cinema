package cinema.controller;

import cinema.data.Order;
import cinema.data.dto.CinemaQueryDTO;
import cinema.data.dto.PurchaseQueryDTO;
import cinema.data.dto.RevokeQueryDTO;
import cinema.repository.CinemaRepository;
import cinema.service.CinemaDataService;
import cinema.service.CinemaOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cinema")
@AllArgsConstructor
public class CinemaDataController {
    private CinemaDataService cinemaDataService;
    private CinemaOrderService cinemaOrderService;

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public CinemaQueryDTO getCinemaData(@RequestBody CinemaQueryDTO query) {
        return cinemaDataService.getCinemaData(query);
    }

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public CinemaRepository.CinemaOrders getAllOrders() {
        return cinemaDataService.getAllOrders();
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public Order processPurchase(@RequestBody PurchaseQueryDTO purchase) {
        return cinemaOrderService.processPurchase(purchase);
    }

    @RequestMapping(value = "/order/revoke", method = RequestMethod.POST)
    public RevokeQueryDTO revokeOrder(@RequestBody RevokeQueryDTO revokeQuery) {
        return cinemaOrderService.revokeOrder(revokeQuery);
    }
}
