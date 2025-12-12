package com.example.WebKtx.config;

import com.example.WebKtx.common.EncrDecrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINTS = {
            "/webktx/vnpay/verify","/webktx/vnpay/ipn","/webktx/authentication/login", "/webktx/authentication/introspect", "/webktx/authentication/logout", "/webktx/authentication/refresh"
    };

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity)
            throws Exception {
        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request ->
                                request.requestMatchers(PUBLIC_ENDPOINTS)
                                        .permitAll()
                                        // Áp dụng quy tắc này cho các request có method POST và endpoint là /users,
                                        // /auth/token, /auth/introspect
                                        // .requestMatchers(HttpMethod.GET, "/users")
                                        // phân quyền trên endpoint :
                                        // .hasAuthority("SCOPE_ADMIN")//endpoint /user có method get và có scope admin
                                        // mới truy cập vào đc
                                        // .hasRole(Role.ADMIN.name())//endpoint /user có method get và có role admin
                                        // mới truy cập vào đc
                                        .anyRequest()
                                        .authenticated());
        httpSecurity.oauth2ResourceServer(
                oauth2 ->
                oauth2
                        .bearerTokenResolver(bearerTokenResolver())
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(
                                new JwtAuthenticationEntryPoint())
                );
        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        //        Trích xuất role/authority từ JWT (ví dụ: ROLE_ADMIN, SCOPE_USER,...).
        //        Tạo ra một đối tượng Authentication chứa thông tin người dùng.
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
                new JwtGrantedAuthoritiesConverter(); // Dùng để chuyển đổi các claim trong JWT (như scope, roles) thành
        // danh sách GrantedAuthority mà Spring Security sử dụng để phân
        // quyền.
        // jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");//đổi SCOPE_ thành ROLE_ cho dễ hiểu//tự động gán
        // Prefix là ROLE_ vào tất cả các scope trong token
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter =
                new JwtAuthenticationConverter(); // Convert Jwt → Authentication
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                jwtGrantedAuthoritiesConverter); // set lại JwtGrantedAuthoritiesConverter

        return jwtAuthenticationConverter;
    }

    @Bean
    // lần
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowCredentials(true);
        cfg.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "http://localhost:5173",
                "https://your-frontend-domain.com"
        ));

        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Authorization","Content-Type"));
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    BearerTokenResolver bearerTokenResolver() {
        return request -> {
            if (request.getCookies() == null) return null;
            for (var c : request.getCookies()) {
                if ("auth".equals(c.getName())) {
//                    return c.getValue();
                    return EncrDecrUtils.decrypt(c.getValue());
                }
            }
            return null;
        };
    }
}
