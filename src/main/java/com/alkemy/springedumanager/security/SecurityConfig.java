package com.alkemy.springedumanager.security;

//import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Control de acceso con Spring Security                    ║
// ║                                                           ║
// ║  Define QUÉ rutas son públicas, cuáles requieren login    ║
// ║  y cuáles requieren un rol específico.                    ║
// ║                                                           ║
// ║  Roles del sistema:                                       ║
// ║    ROLE_ADMIN → puede crear/editar/eliminar todo          ║
// ║    ROLE_USER  → solo puede ver sus propios datos          ║
// ╚═══════════════════════════════════════════════════════════╝
@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // habilita @PreAuthorize en los controllers
//@RequiredArgsConstructor
public class SecurityConfig {

    // BCrypt es el estándar para hashear contraseñas.
    // El factor 12 significa 2^12 iteraciones — intencionalmente lento.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth

                // ── Rutas públicas ──────────────────────────────
                .requestMatchers(
                    "/login", "/registro",
                    "/css/**", "/js/**", "/img/**",
                    "/h2-console/**"       // consola H2 accesible sin login (solo dev)
                ).permitAll()

                // ── Solo ADMIN puede crear/editar/eliminar cursos ──
                .requestMatchers("/cursos/nuevo", "/cursos/editar/**", "/cursos/eliminar/**")
                    .hasRole("ADMIN")

                // ── Solo ADMIN gestiona estudiantes ────────────
                .requestMatchers("/estudiantes/nuevo", "/estudiantes/editar/**", "/estudiantes/eliminar/**")
                    .hasRole("ADMIN")

                // ── Solo ADMIN gestiona usuarios ────────────────
                // Segunda línea de defensa además del @PreAuthorize en UsuarioController.
                // Garantiza que ningún USER pueda acceder a /usuarios/** aunque
                // llegue directamente por URL sin pasar por el controller.
                .requestMatchers("/usuarios/**").hasRole("ADMIN")

                // ── API REST: cualquier usuario autenticado ─────
                // Los endpoints REST también quedan protegidos.
                .requestMatchers("/api/**").authenticated()

                // ── Todo lo demás requiere estar logueado ───────
                .anyRequest().authenticated()
            )

            // ── Formulario de login ──────────────────────────────
            // Spring Security maneja el POST /login automáticamente.
            // Solo necesitas crear la página HTML en templates/auth/login.html.
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )

            // ── Logout ───────────────────────────────────────────
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // Necesario para que funcione la consola H2 en desarrollo.
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin()) // permite iframes de H2
            );

        return http.build();
    }
}
