package com.hungnguyen.coffee.restapitjava.configuration;


import com.hungnguyen.coffee.restapitjava.service.JWTService;
import com.hungnguyen.coffee.restapitjava.service.UserService;
import com.hungnguyen.coffee.restapitjava.utils.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

import static com.hungnguyen.coffee.restapitjava.utils.TokenType.ACCESS_TOKEN;

// hứng ca request vào ứng dun, xử lí xong r chuyển sang api
@Component
@Slf4j
@RequiredArgsConstructor
public class PreFilter extends OncePerRequestFilter implements WebMvcConfigurer {

    private final UserService userService;
    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("doFilterInternal");

        final  String authorization = request.getHeader("Authorization");

        if(StringUtils.isBlank(authorization) ||  !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authorization.substring(7);

        final String username = jwtService.extractUsername(token, ACCESS_TOKEN);

        if(StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(username);

            if(jwtService.isValid(token,userDetails,
                    TokenType.ACCESS_TOKEN)) {
                SecurityContext context = SecurityContextHolder.getContext();

                UsernamePasswordAuthenticationToken authenticationToken =  new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }
}
