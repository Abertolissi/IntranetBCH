package com.banco.intranet.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.Collections;

/**
 * Configuración CORS para permitir acceso desde el frontend Angular
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${app.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Configurar orígenes permitidos
        String[] origins = allowedOrigins.split(",");
        configuration.setAllowedOrigins(Arrays.stream(origins).map(String::trim).toList());
        
        // Configurar métodos HTTP permitidos
        String[] methods = allowedMethods.split(",");
        configuration.setAllowedMethods(Arrays.stream(methods).map(String::trim).toList());
        
        // Configurar headers permitidos
        if ("*".equals(allowedHeaders.trim())) {
            configuration.setAllowedHeaders(Collections.singletonList("*"));
        } else {
            String[] headers = allowedHeaders.split(",");
            configuration.setAllowedHeaders(Arrays.stream(headers).map(String::trim).toList());
        }
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(maxAge);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
