package com.thamienotes.notetaking.securitystuff;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    @Autowired JwtUtil jwtUtil;
    @Autowired UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if(authHeader != null && authHeader.startsWith("Bearer")){
            String jwt = authHeader.substring(7);
            String username = jwtUtil.extractUsername(jwt);

            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails =  userDetailsService.loadUserByUsername(username);
                if(jwtUtil.isTokenValid(jwt, userDetails)){
                
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken
                                                                                (userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);

        
    }
    
}
