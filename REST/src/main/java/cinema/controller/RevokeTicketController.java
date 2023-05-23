package cinema.controller;

import cinema.data.dto.RevokeQueryDTO;
import cinema.service.RevokeTicketService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class RevokeTicketController {
    private RevokeTicketService revokeTicketService;

    @PostMapping(value = "/order/revoke", produces = MediaType.APPLICATION_JSON_VALUE)
    public RevokeQueryDTO revokeOrder(@RequestBody RevokeQueryDTO revokeQuery) {
        return revokeTicketService.revokeOrder(revokeQuery);
    }
}
