package com.example.QuizApplicationImplemented.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CusUserDetailService userDetailService;

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//        final String jwt;
//        final String username;
//
//        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWithIgnoreCase(authHeader,"Bearer ")){
//            filterChain.doFilter(request , response);
//            return;
//        }
//
//        jwt = authHeader.substring(7);
//        username = jwtUtil.extractUsername(jwt);
//
//        if ((!StringUtils.isEmpty(username)) && SecurityContextHolder.getContext().getAuthentication() == null){
//            UserDetails userDetails = userDetailService.loadUserByUsername(username);
//
//            if (jwtUtil.isTokenValid(jwt,userDetails)){
//                SecurityContext context = SecurityContextHolder.createEmptyContext();
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails , null , userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                context.setAuthentication(authToken);
//                SecurityContextHolder.setContext(context);
//            }
//        }
//
//        filterChain.doFilter(request,response);
//    }

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request , HttpServletResponse response , FilterChain filterChain) throws ServletException , IOException{
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        String requestPath = request.getServletPath();
        if (isPublicEndpoint(requestPath)){
            filterChain.doFilter(request , response);
            return;
        }

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request , response);
        }

        try{
            jwt = authHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
            if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = userDetailService.loadUserByUsername(username);

                if (jwtUtil.isTokenValid(jwt , userDetails)){
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails , null , userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authenticationToken);
                    SecurityContextHolder.setContext(context);

                    logger.debug("User '{}' authenticated successfully " , username);

                }else {
                    logger.warn("Invalid JWT token for user : {}" , username);
                }
            }
        }catch (UsernameNotFoundException es){
            logger.warn("User not found during JWT authentication : {}" , es.getMessage());
        }catch (Exception ex){
            logger.error("JWT authentication error : {}" , ex.getMessage());
            SecurityContextHolder.clearContext();
        }
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/signup") ||
                path.startsWith("/api/auth/forgot-password") ||
                path.startsWith("/api/auth/reset-password") ||
                path.equals("/error") ||
                path.startsWith("/actuator/health");
    }

}
