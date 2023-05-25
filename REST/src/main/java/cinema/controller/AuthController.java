package cinema.controller;

import cinema.data.dto.AccountDTO;
import cinema.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private AccountService accountService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public AccountDTO onAccountRegister(@RequestBody AccountDTO account){
        return accountService.processRegister(account);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public AccountDTO onAccountLogin(@RequestBody AccountDTO account){
        return accountService.processLogin(account);
    }
}
