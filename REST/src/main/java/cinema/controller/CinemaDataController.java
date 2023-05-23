package cinema.controller;

import cinema.data.Order;
import cinema.data.dto.CinemaQueryDTO;
import cinema.service.CinemaDataService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
public class CinemaDataController {
    private CinemaDataService cinemaDataService;

    @GetMapping("/data")
    public CinemaQueryDTO getCinemaData(@RequestBody CinemaQueryDTO query) {
        return cinemaDataService.getCinemaData(query);
    }

    @GetMapping("/orders")
    public Map<String, Order> getStats() {
        return cinemaDataService.getAllOrders();
    }
}
