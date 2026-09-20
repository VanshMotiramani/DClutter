package com.declutter.dclutter.security.jwt;

import com.declutter.dclutter.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    @Value("${app.jwt.cookie-name}")
    private String jwtCookieName;

    // ✨ Generate JWT Cookie
    public ResponseCookie generateJwtCookie(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());

        return ResponseCookie.from(jwtCookieName, jwt)
                .path("/api")
                .maxAge(24 * 60 * 60)  // 24 hours
                .httpOnly(true)
                .secure(false)  // Set to true in production with HTTPS
                .sameSite("Lax")
                .build();
    }

    // ✨ Get JWT from Cookie
    public String getJwtFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    // ✨ Generate Clean Cookie (for logout)
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookieName, "")
                .path("/api")
                .maxAge(0)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .build();
    }

    // Generate JWT token from username
    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // Get signing key
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Get username from JWT token
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Validate JWT token
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}