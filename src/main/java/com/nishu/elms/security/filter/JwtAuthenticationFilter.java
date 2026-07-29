package com.nishu.elms.security.filter;

import com.nishu.elms.security.jwt.JwtService;
import com.nishu.elms.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        //read authorization header
        final String authHeader =
                request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);
        //check if header contains bearer token
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        //extract token
        final String jwt = authHeader.substring(7);

        //extract username/email from token
        final String userEmail = jwtService.extractUsername(jwt);
        System.out.println("User Email: " + userEmail);
        //check whether user is not already authenticated
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            //load user from db
            UserDetails userDetails= userDetailsService.loadUserByUsername(userEmail);
            //validate jwt
            System.out.println(
                    "User Authorities: "
                            + userDetails.getAuthorities()
            );
            if(jwtService.isTokenValid(jwt,userDetails)){
                System.out.println("JWT IS VALID");
                //create authentication object
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

                //add request details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //tell spring security user is authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        //continue requet
        filterChain.doFilter(request,response);

    }
}
