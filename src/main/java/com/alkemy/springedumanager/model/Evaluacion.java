package com.alkemy.springedumanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "evaluaciones")
@Data
@NoArgsConstructor
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la evaluación es obligatorio")
    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    // Nota en escala 1.0 a 7.0 (escala chilena)
    @NotNull(message = "La nota es obligatoria")
    @DecimalMin(value = "1.0", message = "La nota mínima es 1.0")
    @DecimalMax(value = "7.0", message = "La nota máxima es 7.0")
    @Column(nullable = false)
    private Double nota;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    // Tipo de evaluación
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoEvaluacion tipo = TipoEvaluacion.PRUEBA;

    // Muchas evaluaciones pertenecen a un curso
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    // Muchas evaluaciones pertenecen a un estudiante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    public enum TipoEvaluacion {
        TRABAJO, CONTROL, PRUEBA, DISERTACION, PROYECTO, EVALUACION
    }

    // Indica si la nota es suficiente (>= 4.0 en escala chilena)
    @Transient
    public boolean isAprobado() {
        return nota != null && nota >= 4.0;
    }
}
