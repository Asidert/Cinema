package tests.cinema;

import cinema.Main;
import cinema.repository.AccountRepository;
import cinema.repository.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoConfiguration
@SpringBootTest(classes = {Main.class})
public class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testCreateUser() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode("pwd");

        Account newAccount = new Account("Bruh", "mail@mail.ru", password);
        Account savedAccount = accountRepository.save(newAccount);

        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount.getId()).isGreaterThan(0);
    }

    @Test
    public void testAuth() {
        Account account = accountRepository.getByUsername("Bruh");

        assertThat(account).isNotNull();
        assertThat(account.getPassword()).isEqualTo("$2a$10$/vTXjXj4sKG5fFH87nOiwOMAqd8qi.yRwiK.Gi.sc.90oR9eXQASG");
    }
}