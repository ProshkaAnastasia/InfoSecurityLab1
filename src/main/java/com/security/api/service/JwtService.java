package com.security.api.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {
  
  private final String secret = "VmVyeVNlY3JldEtleUZvckp3dERvbnRTaGFyZTIwMjU="; // base64
  
  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
  }
  
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }
  
  public <T> T extractClaim(String token, Function<Claims, T> f) {
    return f.apply(allClaims(token));
  }
  
  private Claims allClaims(String token) {
    return Jwts.parser()
      .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }
  
  public String generateToken(UserDetails ud) {
    return Jwts.builder()
      .subject(ud.getUsername())
      .issuedAt(new Date())
      .expiration(new Date(System.currentTimeMillis() + 3600000))
      .signWith(key(), SignatureAlgorithm.HS256)
      .compact();
  }
  
  public boolean isTokenValid(String token, UserDetails u) {
    return u.getUsername().equals(extractUsername(token)) && !isExpired(token);
  }
  
  private boolean isExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
  }
}
