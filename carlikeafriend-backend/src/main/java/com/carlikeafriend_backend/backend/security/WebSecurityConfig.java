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

                                // ========================================================================
                                // 1. EXCEPCIONES ESPECÍFICAS (Deben ir ANTES de las reglas públicas)
                                // ========================================================================

                                // Proteger favoritos de la regla pública de productos
                                .requestMatchers("/carlikeafriend/products/favorites/**").authenticated()

                                // Tarifa de transferencia específica de sucursal
                                .requestMatchers(HttpMethod.GET, "/carlikeafriend/branches/{branchId}/transfer-fees")
                                .hasAnyAuthority("ROLE_ADMIN", "READ_BRANCH")

                                // ========================================================================
                                // 2. ENDPOINTS PÚBLICOS (Sin autenticación, acceso global)
                                // ========================================================================

                                .requestMatchers(
                                        "/carlikeafriend/auth/register",
                                        "/carlikeafriend/auth/login",
                                        "/carlikeafriend/users/email/resend-confirmation"
                                ).permitAll()

                                .requestMatchers(HttpMethod.GET,
                                        "/",
                                        "/index.html",
                                        "/assets/**",
                                        "/product-details/{id}" // <-- SeoController
                                ).permitAll()

                                .requestMatchers(HttpMethod.GET,
                                        "/carlikeafriend/products/**",
                                        "/carlikeafriend/products/recommended-products",
                                        "/carlikeafriend/categories/**",
                                        "/carlikeafriend/features/**",
                                        "/carlikeafriend/addons/**",
                                        "/carlikeafriend/policies/**",
                                        "/carlikeafriend/branches/**",
                                        "/carlikeafriend/transfer-fees/**",
                                        "/carlikeafriend/cities/**",
                                        "/carlikeafriend/makes/**",
                                        "/carlikeafriend/policy-types/**",
                                        "/carlikeafriend/public/financial-config",
                                        "/carlikeafriend/reviews/*/products",
                                        "/carlikeafriend/reservations/*/blocked-dates"
                                ).permitAll()

                                // ========================================================================
                                // 3. ENDPOINTS DE ADMINISTRACIÓN GLOBAL Y METADATOS
                                // ========================================================================

                                .requestMatchers("/carlikeafriend/roles/**").hasRole("ADMIN")
                                .requestMatchers("/carlikeafriend/permissions/**").hasRole("ADMIN")
                                .requestMatchers("/carlikeafriend/metadata/vehicle-statuses").hasRole("ADMIN")

                                // ========================================================================
                                // 4. ENDPOINTS DE GESTIÓN OPERATIVA
                                // ========================================================================

                                // -- Productos --
                                .requestMatchers(HttpMethod.POST, "/carlikeafriend/products").hasAnyAuthority("ROLE_ADMIN", "CREATE_PRODUCT")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/products/**").hasAnyAuthority("ROLE_ADMIN", "UPDATE_PRODUCT")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/products/**").hasAnyAuthority("ROLE_ADMIN", "DELETE_PRODUCT")

                                // -- Categorías --
                                .requestMatchers(HttpMethod.POST, "/carlikeafriend/categories").hasAnyAuthority("ROLE_ADMIN", "CREATE_CATEGORY")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/categories/**").hasAnyAuthority("ROLE_ADMIN", "UPDATE_CATEGORY")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/categories/**").hasAnyAuthority("ROLE_ADMIN", "DELETE_CATEGORY")

                                // -- Características --
                                .requestMatchers(HttpMethod.POST, "/carlikeafriend/features").hasAnyAuthority("ROLE_ADMIN", "CREATE_FEATURE")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/features/**").hasAnyAuthority("ROLE_ADMIN", "UPDATE_FEATURE")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/features/**").hasAnyAuthority("ROLE_ADMIN", "DELETE_FEATURE")

                                // -- Sucursales --
                                .requestMatchers(HttpMethod.POST, "/carlikeafriend/branches").hasAnyAuthority("ROLE_ADMIN", "CREATE_BRANCH")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/branches/**").hasAnyAuthority("ROLE_ADMIN", "UPDATE_BRANCH")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/branches/**").hasAnyAuthority("ROLE_ADMIN", "DELETE_BRANCH")

                                // -- Extras --
                                .requestMatchers(HttpMethod.POST, "/carlikeafriend/addons").hasAnyAuthority("ROLE_ADMIN", "CREATE_ADDON")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/addons/**").hasAnyAuthority("ROLE_ADMIN", "UPDATE_ADDON")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/addons/**").hasAnyAuthority("ROLE_ADMIN", "DELETE_ADDON")

                                // -- Inventario de Extras en Sucursales --
                                .requestMatchers(HttpMethod.GET, "/carlikeafriend/inventory").hasAnyAuthority("ROLE_ADMIN", "READ_INVENTORY")
                                .requestMatchers(HttpMethod.POST, "/carlikeafriend/inventory").hasAnyAuthority("ROLE_ADMIN", "CREATE_INVENTORY")

                                // -- Configuración Financiera --
                                .requestMatchers(HttpMethod.GET, "/carlikeafriend/financial-config").hasAnyAuthority("ROLE_ADMIN", "READ_FINANCIAL_CONFIG")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/financial-config").hasAnyAuthority("ROLE_ADMIN", "UPDATE_FINANCIAL_CONFIG")

                                // -- Tarifas de transferencia --
                                .requestMatchers(HttpMethod.POST, "/carlikeafriend/transfer-fees").hasAnyAuthority("ROLE_ADMIN", "CREATE_BRANCH_TRANSFER_FEE")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/transfer-fees/**").hasAnyAuthority("ROLE_ADMIN", "UPDATE_BRANCH_TRANSFER_FEE")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/transfer-fees/**").hasAnyAuthority("ROLE_ADMIN", "DELETE_BRANCH_TRANSFER_FEE")

                                // -- Ciudades --
                                .requestMatchers(HttpMethod.POST, "/carlikeafriend/cities").hasAnyAuthority("ROLE_ADMIN", "CREATE_CITY")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/cities/**").hasAnyAuthority("ROLE_ADMIN", "UPDATE_CITY")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/cities/**").hasAnyAuthority("ROLE_ADMIN", "DELETE_CITY")

                                // -- Marcas --
                                .requestMatchers(HttpMethod.POST, "/carlikeafriend/makes").hasAnyAuthority("ROLE_ADMIN", "CREATE_MAKE")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/makes/**").hasAnyAuthority("ROLE_ADMIN", "UPDATE_MAKE")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/makes/**").hasAnyAuthority("ROLE_ADMIN", "DELETE_MAKE")

                                // -- Tipos de Políticas --
                                .requestMatchers(HttpMethod.POST, "/carlikeafriend/policy-types").hasAnyAuthority("ROLE_ADMIN", "CREATE_POLICY_TYPE")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/policy-types/**").hasAnyAuthority("ROLE_ADMIN", "UPDATE_POLICY_TYPE")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/policy-types/**").hasAnyAuthority("ROLE_ADMIN", "DELETE_POLICY_TYPE")

                                // -- Políticas --
                                .requestMatchers(HttpMethod.POST, "/carlikeafriend/policies").hasAnyAuthority("ROLE_ADMIN", "CREATE_POLICY")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/policies/**").hasAnyAuthority("ROLE_ADMIN", "UPDATE_POLICY")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/policies/**").hasAnyAuthority("ROLE_ADMIN", "DELETE_POLICY")

                                // -- Vehículos --
                                .requestMatchers(HttpMethod.GET, "/carlikeafriend/vehicles/**").hasAnyAuthority("ROLE_ADMIN", "READ_VEHICLE") //Revisar
                                .requestMatchers(HttpMethod.POST, "/carlikeafriend/vehicles").hasAnyAuthority("ROLE_ADMIN", "CREATE_VEHICLE")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/vehicles/**").hasAnyAuthority("ROLE_ADMIN", "UPDATE_VEHICLE")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/vehicles/**").hasAnyAuthority("ROLE_ADMIN", "DELETE_VEHICLE")
                                .requestMatchers(HttpMethod.PATCH, "/carlikeafriend/vehicles/restore/**").hasAnyAuthority("ROLE_ADMIN", "UPDATE_VEHICLE")

                                // -- Mantenimientos --
                                .requestMatchers("/carlikeafriend/vehicles/*/start-maintenance",
                                        "/carlikeafriend/vehicles/*/maintenances",
                                        "/carlikeafriend/maintenances/**")
                                .hasAnyAuthority("ROLE_ADMIN", "MANAGE_MAINTENANCE")

                                // -- Inspecciones --
                                .requestMatchers(HttpMethod.GET, "/carlikeafriend/inspections/**").hasAnyAuthority("ROLE_ADMIN", "READ_INSPECTION")
                                .requestMatchers(HttpMethod.POST, "/carlikeafriend/inspections/**").hasAnyAuthority("ROLE_ADMIN", "CREATE_INSPECTION")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/inspections/**").hasAnyAuthority("ROLE_ADMIN", "UPDATE_INSPECTION")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/inspections/**").hasAnyAuthority("ROLE_ADMIN", "DELETE_INSPECTION")

                                // ========================================================================
                                // 5. GESTIÓN DE CUENTA DEL CLIENTE Y AUTH
                                // ========================================================================

                                // Debe ir antes del bloqueo global de usuarios para permitir que los clientes editen su propio perfil.
                                .requestMatchers("/carlikeafriend/users/account/**").authenticated()

                                .requestMatchers(HttpMethod.GET, "/carlikeafriend/auth/me").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/auth/change-password").authenticated()

                                // ========================================================================
                                // 6. GESTIÓN GLOBAL DE USUARIOS (Solo Admin)
                                // ========================================================================

                                .requestMatchers(HttpMethod.GET, "/carlikeafriend/users", "/carlikeafriend/users/{id}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/carlikeafriend/users/{id}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/carlikeafriend/users/{id}").hasRole("ADMIN")

                                // ========================================================================
                                // 7. DEFAULT (Reservas, reseñas y cualquier otra petición no cubierta)
                                // ========================================================================

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
