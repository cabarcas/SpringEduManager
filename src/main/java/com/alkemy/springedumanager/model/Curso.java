package com.alkemy.springedumanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Entidad Curso                                            ║
// ║  Hibernate crea la tabla "cursos" y la tabla intermedia   ║
// ║  "curso_estudiantes" a partir de las anotaciones JPA.     ║
// ╚═══════════════════════════════════════════════════════════╝
@Entity
@Table(name = "cursos")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Curso {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del curso no puede estar vacío")
    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @NotNull(message = "La duración es requerida")
    @Column(nullable = false)
    private Integer duracionHoras;

    // Estado del curso: ACTIVO o INACTIVO.
    @Column(nullable = false)
    private boolean activo = true;

    // Tabla intermedia generada automáticamente por Hibernate.
    // Genera: curso_estudiantes(curso_id, estudiante_id)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "curso_estudiantes",
        joinColumns = @JoinColumn(name = "curso_id"),
        inverseJoinColumns = @JoinColumn(name = "estudiante_id")
    )
    private Set<Estudiante> estudiantes = new HashSet<>();

    // Relación inversa con PlanEstudio.
    // mappedBy="cursos" indica que PlanEstudio es el dueño de la relación
    // y es quien tiene el @JoinTable en la tabla plan_cursos.
    // Un curso puede pertenecer a múltiples planes
    // (Ej: Matemáticas en plan 1° Básico y plan 2° Básico).
    @ManyToMany(mappedBy = "cursos", fetch = FetchType.LAZY)
    private Set<PlanEstudio> planesEstudio = new HashSet<>();
}
