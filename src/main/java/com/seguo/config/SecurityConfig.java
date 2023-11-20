package com.seguo.config;

import com.seguo.service.JpaUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity//允许使用 @PreAuthorize 和 @PostAuthorize 注解来保护单个方法。
public class SecurityConfig {
    @Autowired
    CustomLogoutSuccessHandler customLogoutSuccessHandler;
    @Autowired
    JpaUserDetailsService jpaUserDetailsService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
//                        .failureForwardUrl("/")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/user/dashboard")
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .deleteCookies("JSESSIONID")
//                        .logoutSuccessUrl("/")
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                )
                .rememberMe(rm -> rm
                        .rememberMeParameter("remember-me")
                )
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
//                        .requestMatchers("/","/login","/build/**","/vendor/**").permitAll()
                        .requestMatchers("/admin/**").hasAnyRole("admin")
                        .anyRequest().permitAll()
                );

        return http.build();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(jpaUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
