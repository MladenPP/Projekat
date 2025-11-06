package com.example.Projekat.config.security.jwt;

import com.example.Projekat.db.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.HashMap;


@Component
public class JwtTokenUtil
{

    private static final String SECRET_KEY = "t/49gKC8uZxdQWW1DkX5gxnVTxa1fpO70KQ4rJvkPhQnfABH783A1l/76Drv6zPGs2L81W+aWfoQZ8vdDWXFHg==";


    private static final long EXPIRATION_TIME = 1000 * 60 * 30;


    private SecretKey getSigningKey()
    {
        final byte[] encodedKey = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(encodedKey);
    }


    private Claims extractAllClaims(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    private <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    public String extractUsername(final String token)
    {
        return extractClaim(token, Claims::getSubject);
    }


    private Date extractExpiration(final String token)
    {
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(final String token)
    {
        return extractExpiration(token).before(new Date());
    }


    public String generateToken(final Map<String, Object> extraClaims, final UserDetails userDetails) {
        final Date now = new Date();


        final Map<String, Object> claims = (extraClaims != null) ? extraClaims : new HashMap<>();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    public String generatePasswordResetToken(UserEntity user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("type", "password_reset")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30 min
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }



    public Boolean validateToken(final String token, final UserDetails userDetails)
    {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
