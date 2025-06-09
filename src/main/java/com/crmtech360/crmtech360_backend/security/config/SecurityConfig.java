package com.crmtech360.crmtech360_backend.security.config;

import com.crmtech360.crmtech360_backend.security.jwt.JwtAuthenticationEntryPoint;
import com.crmtech360.crmtech360_backend.security.jwt.JwtRequestFilter;
import com.crmtech360.crmtech360_backend.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Se mantiene si se usa @PreAuthorize/@PostAuthorize
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
// import java.util.List; // Esta importación no es estrictamente necesaria aquí si List no se usa directamente en la clase.

@Configuration
@EnableWebSecurity
// Se mantiene prePostEnabled si planeas usar @PreAuthorize o @PostAuthorize
// Se eliminan securedEnabled y jsr250Enabled ya que no usaremos @Secured ni @RolesAllowed
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;
    // UserDetailsServiceImpl se inyecta porque el AuthenticationManager lo necesita internamente
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtRequestFilter jwtRequestFilter,
                          UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtRequestFilter = jwtRequestFilter;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Configuración de CORS para permitir solicitudes desde el frontend
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Deshabilitar CSRF para APIs REST sin sesión
                .csrf(AbstractHttpConfigurer::disable)
                // Manejo de excepciones para autenticación (ej. 401 Unauthorized)
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                // Configurar la gestión de sesiones como STATELESS (sin estado), ideal para JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configuración de las reglas de autorización para las solicitudes HTTP
                .authorizeHttpRequests(auth -> auth
                        // Permitir acceso a todas las rutas de autenticación sin necesidad de token
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Permitir acceso a la documentación de Swagger UI sin necesidad de token
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()
                        // Todas las demás solicitudes (cualquier otra ruta) requieren autenticación
                        .anyRequest().authenticated()
                );

        // Añadir el filtro JWT antes del filtro de autenticación de usuario y contraseña de Spring Security
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // *** IMPORTANTE: Configura aquí los orígenes permitidos para tu frontend ***
        // Asegúrate de que esta URL coincida exactamente con la URL de tu aplicación frontend.
        // Por ejemplo, si tu frontend corre en Vite en el puerto 5173, usa "http://localhost:5173".
        // Si tienes más de un origen, puedes añadirlos a la lista: Arrays.asList("http://localhost:5173", "https://tu-dominio-frontend.com")
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // <-- VERIFICA ESTE PUERTO

        // Métodos HTTP permitidos para las solicitudes CORS
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // Cabeceras HTTP permitidas en las solicitudes CORS
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",          // Necesario para enviar el token JWT
                "Cache-Control",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Origin",                 // Usado en solicitudes preflight
                "Access-Control-Request-Method", // Usado en solicitudes preflight
                "Access-Control-Request-Headers"  // Usado en solicitudes preflight
        ));
        // Cabeceras que el navegador puede exponer a la aplicación cliente
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        // Permitir el envío de credenciales (cookies, encabezados de autorización)
        configuration.setAllowCredentials(true);
        // Tiempo máximo en segundos que la configuración CORS puede ser cacheados por el cliente
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicar esta configuración CORS a todas las rutas de la API
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}