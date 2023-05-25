package cinema.security.jwt;

import cinema.data.dto.AccountDTO;
import cinema.exception.AuthException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTUtils implements Serializable {
    @Value("${cinema.data.JWTSecret}")
    private String SECRET;
    @Value("${cinema.data.JWTExpiration}")
    private Long EXPIRATION;
    private Key KEY;
    private JwtParser JWT_PARSER;

    @PostConstruct
    private void init() {
        KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        JWT_PARSER = Jwts.parserBuilder().setSigningKey(KEY).build();
    }

    public String validateToken(String token) {
        try {
            JWT_PARSER.parseClaimsJws(token);
            return null;
        } catch (ExpiredJwtException e) {
            return "Expired token detected, please, get a new one by /api/auth/login";
        } catch (Exception e) {
            return "Unsupported bearer token!";
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getAllClaimsFromToken(token));
    }

    private Claims getAllClaimsFromToken(String token) {
        return JWT_PARSER.parseClaimsJws(token).getBody();
    }

    public String generateToken(String name) {
        Map<String, Object> claims = new HashMap<>();
        return performGenerateToken(claims, name);
    }

    private String performGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY).compact();
    }

    public boolean validateBearer(String token, UserDetails account) {
        final String username = getUsernameFromToken(token);
        return username.equals(account.getUsername());
    }
}
