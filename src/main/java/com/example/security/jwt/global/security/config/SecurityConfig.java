package com.example.security.jwt.global.security.config;

import com.example.security.jwt.global.security.handler.JwtAccessDeniedHandler;
import com.example.security.jwt.global.security.handler.JwtAuthenticationEntryPoint;
import com.example.security.jwt.global.security.jwt.CustomJwtAuthenticationConverter;
import com.example.security.jwt.global.security.jwt.CustomJwtGrantedAuthoritiesConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity // 기본적인 웹보안을 활성화하겠다
@EnableMethodSecurity // @PreAuthorize 어노테이션 사용을 위해 선언
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // token을 사용하는 방식이기 때문에 csrf를 disable
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .cors(httpSecurityCorsConfigurer ->
                        httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource())
                )
                .exceptionHandling(exceptionConfig ->
                        exceptionConfig.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .headers(headerConfig -> headerConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(registry ->
                        registry.requestMatchers("/h2/**").permitAll()
                                .requestMatchers("/favicon.ico").permitAll()
                                .requestMatchers("/error").permitAll()

                )
                .authorizeHttpRequests(registry ->  // actuator, swagger 경로, 실무에서는 상황에 따라 적절한 접근제어 필요
                        registry.requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/docs/**").permitAll()
                )
                .authorizeHttpRequests(registry -> // api path
                        registry.requestMatchers("/api/hello").permitAll()
                                .requestMatchers("/api/v1/accounts/token").permitAll() // login
                                .requestMatchers("/api/v1/members").permitAll()
                )
                .authorizeHttpRequests(registry -> registry.anyRequest().authenticated()) // 나머지 경로는 jwt 인증 해야함
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer ->
                        httpSecurityOAuth2ResourceServerConfigurer.jwt(jwtConfigurer ->
                                jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter()) // jwtDecoder 는 알아서 참조해감
                        )
                );

        return httpSecurity.build();
    }

    private CustomJwtAuthenticationConverter jwtAuthenticationConverter() {
        return new CustomJwtAuthenticationConverter(new CustomJwtGrantedAuthoritiesConverter());
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8080"));
        config.addAllowedHeader("*");
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}