package com.alkemy.springedumanager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Entidad Usuario — Spring Security                        ║
// ║  Guarda los usuarios que pueden loguearse.                ║
// ║  Roles disponibles: ROLE_ADMIN, ROLE_USER                 ║
// ╚═══════════════════════════════════════════════════════════╝
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    // Aquí va el hash BCrypt, NUNCA el password en texto plano.
    @Column(nullable = false)
    private String password;

    // "ROLE_ADMIN" o "ROLE_USER".
    // En proyectos grandes esto se normaliza en tabla aparte,
    // pero para este alcance un solo campo es suficiente.
    @Column(nullable = false, length = 30)
    private String rol;

    @Column(nullable = false)
    private boolean activo = true;

    // Vínculo con el estudiante — null si el usuario es ADMIN.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = true)
    private Estudiante estudiante;

    public Usuario(String username, String password, String rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }
}
