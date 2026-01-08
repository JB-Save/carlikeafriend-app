package com.carlikeafriend_backend.backend.security;

import com.carlikeafriend_backend.backend.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita la seguridad a nivel de método si se necesita en el futuro
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    public WebSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                             AuthenticationProvider authenticationProvider,
                             RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                             RestAccessDeniedHandler restAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Habilitar CORS usando la configuración definida en el bean corsConfigurationSource
                .cors(Customizer.withDefaults())
                // Deshabilitar CSRF (Cross-Site Request Forgery) ya que usamos JWT (stateless)
                .csrf(AbstractHttpConfigurer::disable)
                // Configurar manejo de excepciones para REST
                .exceptionHandling(eh -> eh
                        // Maneja 401: No autenticado / Token inválido
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        // Maneja 403: Autenticado pero sin permiso
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                // Definir las reglas de autorización para cada endpoint
                .authorizeHttpRequests(
                        auth -> auth
                                // == ENDPOINTS PÚBLICOS ==
                                // Endpoints públicos que no requieren autenticación
                                .requestMatchers(
                                        "/carlikeafriend/auth/register",
                                        "/carlikeafriend/auth/login",
                                        "/carlikeafriend/users/email/resend-confirmation"
                                ).permitAll()
                                // Endpoints públicos de productos (lectura)
                                .requestMatchers(HttpMethod.GET,
                                        "/carlikeafriend/products/**",
                                        "/carlikeafriend/categories/**",
                                        "/carlikeafriend/features/**",
                                        "/products/recommended-products",
                                        "/products/images/image/{fileName:.+}",
                                        "/features/images/image/{fileName:.+}"
                                ).permitAll()

                                // == ENDPOINTS DE ADMINISTRACIÓN (Roles y Permisos) ==
                                .requestMatchers("/carlikeafriend/roles/**").hasRole("ADMIN")
                                .requestMatchers("/carlikeafriend/permissions/**").hasRole("ADMIN")

                                // == ENDPOINTS DE GESTIÓN (Ej: Productos) ==
                                .requestMatchers(HttpMethod.POST, "/carlikeafriend/products")
                                .hasAnyAuthority("ROLE_ADMIN", "CREATE_PRODUCT")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/products/**")
                                .hasAnyAuthority("ROLE_ADMIN", "UPDATE_PRODUCT")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/products/**")
                                .hasAnyAuthority("ROLE_ADMIN", "DELETE_PRODUCT")

                                // == ENDPOINTS DE GESTIÓN (Ej: Categorías) ==
                                .requestMatchers(HttpMethod.POST, "/carlikeafriend/categories")
                                .hasAnyAuthority("ROLE_ADMIN", "CREATE_CATEGORY")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/categories/**")
                                .hasAnyAuthority("ROLE_ADMIN", "UPDATE_CATEGORY")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/categories/**")
                                .hasAnyAuthority("ROLE_ADMIN", "DELETE_CATEGORY")

                                // == ENDPOINTS DE GESTIÓN (Ej: Características) ==
                                .requestMatchers(HttpMethod.POST, "/carlikeafriend/features")
                                .hasAnyAuthority("ROLE_ADMIN", "CREATE_FEATURE")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/features/**")
                                .hasAnyAuthority("ROLE_ADMIN", "UPDATE_FEATURE")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/features/**")
                                .hasAnyAuthority("ROLE_ADMIN", "DELETE_FEATURE")

                                // == ENDPOINTS DE USUARIOS (El más sensible) ==
                                .requestMatchers(HttpMethod.GET, "/carlikeafriend/users", "/carlikeafriend/users/{id}")
                                .hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/users/{id}")
                                .hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/users/{id}")
                                .hasRole("ADMIN")

                                // El endpoint /me debe estar autenticado.
                                .requestMatchers(HttpMethod.GET, "/carlikeafriend/auth/me").authenticated()

                                // == DEFAULT ==
                                // Todas las demás peticiones deben estar autenticadas
                                .anyRequest().authenticated()
                )
                // Configurar la gestión de sesiones como STATELESS, fundamental para APIs REST con JWT
                .sessionManagement(
                        sessionM -> sessionM
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Establecer el proveedor de autenticación personalizado
                .authenticationProvider(authenticationProvider)
                // Añadir nuestro filtro de autenticación JWT antes del filtro estándar de Spring
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /* Bean para configurar la política de CORS (Cross-Origin Resource Sharing).
      Esto permite que el frontend en React en localhost:5173 se comunique con el backend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Origen permitido de la aplicación de React
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Cabeceras permitidas
        configuration.setAllowedHeaders(List.of("*"));
        // Permitir credenciales (cookies, tokens de autorización)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicar esta configuración a todas las rutas de la aplicación
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
