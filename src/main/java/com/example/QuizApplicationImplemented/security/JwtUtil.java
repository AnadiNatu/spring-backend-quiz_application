package com.example.QuizApplicationImplemented.security;


import com.example.QuizApplicationImplemented.entity.Users;
import com.example.QuizApplicationImplemented.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
//    @Autowired
//    private UserRepository userRepository;
//    private final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
//
//    public String generateToken(String username) {
//
//        Map<String,Object> claims = new HashMap<>();
//        return Jwts.builder()
//                .claims()
//                .add(claims)
//                .subject(username)
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis()+1000*30*60))
//                .and()
//                .signWith(getSigningKey())
//                .compact();
//    }
//
//    private SecretKey getSigningKey() {
//
//        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET);  // this line of code converts the string into a string array for .hmacShaKeyFor() method
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//
//
//    public String extractUsername(String token) {
//        return extractClaim(token , Claims::getSubject);
//    }
//
//    public <T> T extractClaim(String token , Function<Claims,T> claimsResolver){
//
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    private Claims extractAllClaims(String token){
//        return Jwts.parser()
//                .verifyWith(getSigningKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    private boolean isTokenExpired(String token){
//        return  extractExpiration(token).before(new Date());
//    }
//    private Date extractExpiration(String token){
//        return extractClaim(token , Claims::getExpiration);
//    }
//
//
//    public Users getLoggedInUser(){
//
//        Authentication authentcation = SecurityContextHolder.getContext().getAuthentication();
//        if (authentcation != null && authentcation.isAuthenticated()){
//            String username = authentcation.getName();
//            return userRepository.findByUsername(username).orElse(null);
//        }
//        return null;
//    }
//
//    public String getLoggedInUsername(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)){
//            Users users = (Users) authentication.getPrincipal();
//        }
//        return null;
//    }

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret:5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437}")
    private String SECRET;

    @Value("${jwt.expiration:1800000}")
    private Long jwtExpiration;

    public <T> T  extractClaim(String token , Function<Claims , T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token , UserDetails userDetails){
        try{
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        }catch (Exception e){
            return false;
        }
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Users getLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()){
            Object principal = authentication.getPrincipal();
            if (principal instanceof Users){
                return (Users) principal;
            } else if (principal instanceof String username) {
                return userRepository.findByUsername(username).orElse(null);
            }
        }
        return null;
    }

    public String getLoggedInUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()){
            Object principal = authentication.getPrincipal();
            if (principal instanceof Users){
                return ((Users) principal).getUsername();
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null;
    }

    public Long getLoggedInUserId(){

        Users user = getLoggedInUser();
        return user != null ? user.getId() : null;

    }

    public boolean hasRole(String role) {
        Users user = getLoggedInUser();
        return user != null && user.getUserRoles().name().equals(role);
    }

    public String refreshToken(String token){
        try{
            String username = extractUsername(token);
            UserDetails userDetails = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (isTokenValid(token , userDetails)){
                return generateToken(username);
            }
        }catch (Exception e){
            throw new RuntimeException("Failed to refresh token" , e);
        }

        throw new RuntimeException("Invalid token for refresh");
    }

    public String generateToken(String username){
        Users user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Map<String , Object> claims = new HashMap<>();
        claims.put("roles" , List.of(user.getUserRoles().name()));
        claims.put("userId" , user.getId());
        claims.put("name" , user.getName());

        return createToken(claims , username);
    }

    private String createToken(Map<String , Object>claims , String subject){
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte [] keyBytes = Decoders.BASE64URL.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token){
        return extractClaim(token , Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token , Claims::getExpiration);
    }

    public List<String> extractRoles(String token){
        return extractClaim(token , claims -> (List<String>) claims.get("roles"));
    }

    public Long extractUserId(String token){
        return extractClaim(token , claims -> {
            Object userId = claims.get("userId");
            if (userId instanceof Integer){
                return ((Integer) userId).longValue();
            }
            return (Long) userId;
        });
    }


}
