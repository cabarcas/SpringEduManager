package com.alkemy.springedumanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "estudiantes")
@Data
@NoArgsConstructor
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Datos del estudiante ─────────────────────────────────
    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Column(nullable = false, length = 100)
    private String apellido;

    @NotBlank(message = "El RUT no puede estar vacío")
    @Column(unique = true, nullable = false, length = 12)
    private String rut;

    @NotNull(message = "La fecha de nacimiento es requerida")
    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    // Calculada desde fechaNacimiento — no se persiste en BD
    @Transient
    public int getEdad() {
        if (fechaNacimiento == null) return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    // Dirección del núcleo de convivencia del estudiante
    @Column(length = 250)
    private String direccion;

    // ── Nivel y sección ──────────────────────────────────────
    // Valores: "1° Básico", "2° Básico", ..., "8° Básico",
    //          "1° Medio", "2° Medio", "3° Medio", "4° Medio"
    @NotBlank(message = "El nivel es requerido")
    @Column(nullable = false, length = 20)
    private String nivelMineduc;

    // Valores: "A", "B", "C", "D", "E"
    @NotBlank(message = "La sección es requerida")
    @Column(nullable = false, length = 1)
    private String seccion;

    @Column(nullable = false)
    private boolean activo = true;

    // ── Apoderado principal ──────────────────────────────────
    @NotBlank(message = "El nombre del apoderado es requerido")
    @Column(nullable = false, length = 150)
    private String nombreApoderado;
 
    @NotBlank(message = "El vínculo del apoderado es requerido")
    @Column(nullable = false, length = 60)
    private String vinculoApoderado;
 
    @Email(message = "Debe ser un email válido")
    @NotBlank(message = "El email del apoderado es requerido")
    @Column(nullable = false, length = 150)
    private String emailApoderado;
 
    @Column(length = 20)
    private String telefonoApoderado;
 
    // true → vive con el estudiante, usar direccion del estudiante
    // false → tiene dirección propia en direccionApoderado
    @Column(nullable = false)
    private boolean apoderadoMismaDireccion = true;
 
    @Column(length = 250)
    private String direccionApoderado;
 
    // ── Apoderado suplente ───────────────────────────────────
    @Column(length = 150)
    private String nombreApoderadoSuplente;
 
    @Column(length = 60)
    private String vinculoApoderadoSuplente;
 
    @Email(message = "Debe ser un email válido")
    @Column(length = 150)
    private String emailApoderadoSuplente;
 
    @Column(length = 20)
    private String telefonoApoderadoSuplente;
 
    // true → vive con el estudiante
    @Column(nullable = false)
    private boolean suplenteMismaDireccionEstudiante = true;
 
    // true → vive con el apoderado principal
    @Column(nullable = false)
    private boolean suplenteMismaDireccionPrincipal = false;
 
    // Se usa solo si ambos flags anteriores son false
    @Column(length = 250)
    private String direccionApoderadoSuplente;

    // ── Relación inversa con Usuario ─────────────────────────
    @OneToOne(mappedBy = "estudiante", fetch = FetchType.LAZY)
    private Usuario usuario;

    // ── Relación con Curso ───────────────────────────────────
    @ManyToMany(mappedBy = "estudiantes", fetch = FetchType.LAZY)
    private Set<Curso> cursos = new HashSet<>();
}
