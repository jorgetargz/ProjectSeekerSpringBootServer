package com.jorgetargz.projectseekerspringboot.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jorgetargz.projectseekerspringboot.config.properties.SecurityProperties;
import com.jorgetargz.projectseekerspringboot.dto.error.ErrorDTO;
import com.jorgetargz.projectseekerspringboot.filters.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


/**
 * SecurityConfig is a configuration class for the security settings of the application.
 * It handles various security configurations such as CORS, authentication and authorization, and exception handling.
 * This class also sets up the necessary security filters and authentication entry points.
 * <p>
 * &#064;Configuration  Indicates that this class is a configuration class.
 * &#064;EnableWebSecurity  Enables web security for the application.
 * &#064;EnableMethodSecurity  Enables method level security with secured, JSR-250, and pre/post annotations.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    private final SecurityProperties restSecProps;

    private final SecurityFilter securityFilter;

    /**
     * Constructs a new SecurityConfig instance with the specified dependencies.
     *
     * @param objectMapper   The ObjectMapper for JSON processing.
     * @param restSecProps   The security properties for the application.
     * @param securityFilter The security filter for token-based authentication.
     */
    @Autowired
    public SecurityConfig(ObjectMapper objectMapper, SecurityProperties restSecProps, SecurityFilter securityFilter) {
        this.objectMapper = objectMapper;
        this.restSecProps = restSecProps;
        this.securityFilter = securityFilter;
    }

    /**
     * Creates a custom AuthenticationEntryPoint for handling unauthorized access.
     *
     * @return The AuthenticationEntryPoint to use for unauthorized access.
     */
    @Bean
    protected AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (httpServletRequest, httpServletResponse, e) -> {
            int errorCode = 401;
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setTimestamp(new Timestamp(new Date().getTime()));
            errorDTO.setHttpErrorCode(errorCode);
            errorDTO.setMessage("Unauthorized access of protected resource, invalid credentials");
            errorDTO.setDescription("The server could not authorize you to access the URL requested.");
            errorDTO.setFirebaseError((String) httpServletRequest.getAttribute("firebase-error"));

            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.setStatus(errorCode);
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(errorDTO));
        };
    }

    /**
     * Configures CORS settings for the application.
     *
     * @return The CorsConfigurationSource to use for configuring CORS.
     */
    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configures the security filter chain for the application.
     *
     * @param http The HttpSecurity instance for configuring the security filter chain.
     * @return The SecurityFilterChain for the application.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(restAuthenticationEntryPoint()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(restSecProps.getPrivateApis().toArray(String[]::new)).authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                // Disables session creation here since we are creating sessions explicitly in the session rest controller.
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}