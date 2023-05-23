package cinema.service;

import cinema.data.dto.RevokeQueryDTO;
import cinema.repository.CinemaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RevokeTicketService {
    private CinemaRepository cinemaRepository;

    public RevokeQueryDTO revokeOrder(RevokeQueryDTO revokeQuery) {
        return cinemaRepository.revokeOrder(revokeQuery);
    }
}