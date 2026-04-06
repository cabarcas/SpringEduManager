package com.alkemy.springedumanager.security;

import com.alkemy.springedumanager.repository.UsuarioRepository;
import com.alkemy.springedumanager.model.Usuario;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Inicializador de datos de prueba                         ║
// ║                                                           ║
// ║  CommandLineRunner se ejecuta UNA vez al arrancar la      ║
// ║  app. Crea los usuarios de prueba si no existen.          ║
// ║                                                           ║
// ║  Usuarios creados:                                        ║
// ║    admin / admin123  → ROLE_ADMIN                         ║
// ║    user  / user123   → ROLE_USER                          ║
// ╚═══════════════════════════════════════════════════════════╝
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Solo crea los usuarios si la tabla está vacía.
        if (usuarioRepository.count() == 0) {

            // Admin: puede crear, editar y eliminar cursos y estudiantes.
            usuarioRepository.save(new Usuario(
                "admin",
                passwordEncoder.encode("admin123"),   // BCrypt hash
                "ROLE_ADMIN"
            ));

            // User: solo puede ver sus cursos, prácticas y evaluaciones.
            usuarioRepository.save(new Usuario(
                "user",
                passwordEncoder.encode("user123"),
                "ROLE_USER"
            ));

            System.out.println(">>> Usuarios de prueba creados: admin/admin123 | user/user123");
        }
    }
}
