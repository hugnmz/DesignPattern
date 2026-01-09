package com.hungnguyen.coffee.restapitjava.configuration;

import com.hungnguyen.coffee.restapitjava.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("!prod")
@RequiredArgsConstructor
public class AppConfig {

    private final UserService userService;
    private final PreFilter preFilter;
    private String[] WHITE_LIST = {"/auth/**"};


    // thiet lap cors cho cac domain sd api
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings( @NotNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8500")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowCredentials(false)
                        .maxAge(300);
            }
        };
    }

    //ma hoa pass
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //thiet lap kieu stateless: ko luu token phia server
    // nhung api cho phep user request ma ko can token
    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {

        // la noi thiet lap whitelist: cai nao cho phep
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers(WHITE_LIST).permitAll().anyRequest().authenticated()).sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authenticationProvider(provider()).addFilterBefore(preFilter,UsernamePasswordAuthenticationFilter.class); //
        // nhung api truy cap ma ko can token

        return http.build();
    }

    // cac api swagger cho phep dc chay
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return webSecurity ->
            webSecurity.ignoring().requestMatchers("/auth/**"); // cho phep hien thi tren swagger

    }

    // quan li cac use truy cap vao he thong
    @Bean
    public AuthenticationProvider provider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService.userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    // truy cap den tang dao user
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
