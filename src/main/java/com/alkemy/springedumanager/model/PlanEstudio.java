package com.alkemy.springedumanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "planes_estudio")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlanEstudio {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre descriptivo del plan
    // Ej: "Plan de Estudio 1° Básico 2024"
    @NotBlank(message = "El nombre del plan es obligatorio")
    @Column(nullable = false, length = 150)
    private String nombre;

    // Nivel MINEDUC al que aplica este plan
    // Ej: "1° Básico", "2° Básico", etc.
    @NotBlank(message = "El nivel es obligatorio")
    @Column(nullable = false, length = 20)
    private String nivelMineduc;

    @NotNull(message = "El año es obligatorio")
    @Column(nullable = false)
    private Integer anio;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private boolean activo = true;

    // Cursos que componen este plan de estudio
    // ManyToMany: un plan tiene muchos cursos,
    // un curso puede estar en varios planes
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "plan_cursos",
        joinColumns = @JoinColumn(name = "plan_id"),
        inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    private Set<Curso> cursos = new HashSet<>();
}
