package it.univaq.progettotesi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    // Metti la chiave in application.properties e leggila da lì!
    private static final String SECRET = "01234567890123456789012345678901"; // >= 32 chars per HS256

    private SecretKey key() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private JwtParser parser() {
        return Jwts.parser()
                .verifyWith(key())
                .build();
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + 1000L * 60 * 60 * 24 * 30); // 30 giorni

        return Jwts.builder()
                .issuer("webapp")
                .audience().add("api").and()
                .subject(username)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key())
                .compact();
    }
    public boolean validateToken(String token, String expectedUsername) {
        try {
            Claims c = claims(token);
            return expectedUsername.equals(c.getSubject())
                    && c.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            // firma non valida, token scaduto o malformato
            return false;
        }
    }

    public String extractUsername(String token) {
        return claims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return claims(token).getExpiration();
    }

    private Claims claims(String token) {
        return parser().parseSignedClaims(token).getPayload();
    }
}
