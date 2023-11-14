package com.seguo.config;


import com.seguo.thymeleaf.CustomLmsDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThymeleafConfig {
    @Bean
    public CustomLmsDialect customDialect() {
        return new CustomLmsDialect();
    }
}
