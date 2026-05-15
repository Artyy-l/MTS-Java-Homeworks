package com.mipt.todolist.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Component
public class JwtUtils {

    private final String issuer;
    private final Duration expiration;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtUtils(@Value("${app.security.jwt.issuer}") String issuer,
                    @Value("${app.security.jwt.secret}") String secret,
                    @Value("${app.security.jwt.expiration}") Duration expiration) {
        this.issuer = issuer;
        this.expiration = expiration;
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(authentication.getName())
                .withIssuedAt(now)
                .withExpiresAt(now.plus(expiration))
                .withClaim("authorities", authorities)
                .sign(algorithm);
    }

    public DecodedToken verify(String token) {
        DecodedJWT jwt = verifier.verify(token);
        List<String> authorities = jwt.getClaim("authorities").asList(String.class);
        return new DecodedToken(jwt.getSubject(), toAuthorities(authorities));
    }

    private Collection<SimpleGrantedAuthority> toAuthorities(List<String> authorities) {
        if (authorities == null) {
            return List.of();
        }
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public record DecodedToken(String username, Collection<SimpleGrantedAuthority> authorities) {
    }
}
