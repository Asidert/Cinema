package cinema.service;

import cinema.data.Order;
import cinema.data.dto.CinemaQueryDTO;
import cinema.repository.CinemaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class CinemaDataService {
    private CinemaRepository cinemaRepository;

    public CinemaQueryDTO getCinemaData(CinemaQueryDTO query) {
        return cinemaRepository.getCinemaData(query);
    }

    public Map<String, Order> getAllOrders() {
        return cinemaRepository.getOrders();
    }
}