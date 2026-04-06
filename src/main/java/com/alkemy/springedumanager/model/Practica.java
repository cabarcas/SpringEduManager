package com.alkemy.springedumanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "practicas")
@Data
@NoArgsConstructor
public class Practica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la práctica es obligatorio")
    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @NotNull(message = "La fecha de entrega es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaEntrega;

    // Estado del ciclo de vida de la práctica
    // PENDIENTE → ENTREGADA → CORREGIDA
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPractica estado = EstadoPractica.PENDIENTE;

    // Muchas prácticas pertenecen a un curso
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    // Muchas prácticas pertenecen a un estudiante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    public enum EstadoPractica {
        PENDIENTE, ENTREGADA, NO_ENTREGADA, CORREGIDA
    }

    // Nota opcional — se asigna cuando el estado pasa a CORREGIDA
    // En escala chilena 1.0 a 7.0
    @DecimalMin(value = "1.0", message = "La nota mínima es 1.0")
    @DecimalMax(value = "7.0", message = "La nota máxima es 7.0")
    @Column
    private Double nota;

    // Indica si la práctica está aprobada (>= 4.0)
    // Solo tiene sentido cuando estado == CORREGIDA
    @Transient
    public boolean isAprobado() {
        return nota != null && nota >= 4.0;
    }
}
