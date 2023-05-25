package cinema.service;

import cinema.data.dto.AccountDTO;
import cinema.exception.AuthException;
import cinema.repository.AccountRepository;
import cinema.repository.model.Account;
import cinema.security.jwt.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AccountService implements UserDetailsService{
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private JWTUtils utils;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Account account = accountRepository.getByUsername(username);
        if (account == null)
            return null;
        return new User(account.getUsername(), account.getPassword(), new ArrayList<>());
    }

    public AccountDTO processLogin(AccountDTO account) {
        final UserDetails userDetails = loadUserByUsername(account.getUsername());
        if (userDetails != null) {
            tryAuthenticate(account.getUsername(), account.getPassword());
            account.setPassword("********");
            account.setToken(utils.generateToken(account.getUsername()));
            return account;
        }
        throw new AuthException("User not found with username: " + account.getUsername());
    }
    
    public AccountDTO processRegister(AccountDTO account){
        if (accountRepository.getByUsername(account.getUsername()) != null)
            throw new AuthException("There are user with name " + account.getUsername() + " already!");

        testPassword(account.getPassword());
        String encodedPassword = passwordEncoder.encode(account.getPassword());
        Account newAccount = new Account(account.getUsername(), "", encodedPassword);
        accountRepository.save(newAccount);
        account.setPassword("********");
        account.setToken(utils.generateToken(account.getUsername()));
        return account;
    }

    private void testPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new AuthException("Password should be at least 8 symbols!");
        }
    }

    private void tryAuthenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new AuthException("You are banned!");
        } catch (BadCredentialsException e) {
            throw new AuthException("Wrong password!");
        }
    }
}
