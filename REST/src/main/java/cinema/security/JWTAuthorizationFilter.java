package cinema.security;

import java.io.IOException;

import cinema.exception.AuthException;
import cinema.security.jwt.JWTUtils;
import cinema.service.AccountService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    private AccountService accountService;
    @Autowired
    private JWTUtils utils;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.length() < 10) {
            return false;
        }
        path = path.substring(0, 9);
        return "/api/auth".equals(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
            writeErrorMessageInResponse(response, "There are no token!");
            return;
        }

        String token = requestTokenHeader.substring(7);
        String message = utils.validateToken(token);
        if (message != null) {
            writeErrorMessageInResponse(response, message);
            return;
        }

        String username = utils.getUsernameFromToken(token);
        //Here was && SecurityContextHolder.getContext().getAuthentication() != null WHY?!
        if (username == null) {
            writeErrorMessageInResponse(response, "Who are you?!");
            return;
        }
        UserDetails userDetails = accountService.loadUserByUsername(username);
        if (!utils.validateBearer(token, userDetails)) {
            writeErrorMessageInResponse(response, "Expired token or this token not yours!");
            return;
        }

        //What is going on here?!
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        chain.doFilter(request, response);
    }

    private void writeErrorMessageInResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("{\"message\":\""+message+"\"}");
    }
}
