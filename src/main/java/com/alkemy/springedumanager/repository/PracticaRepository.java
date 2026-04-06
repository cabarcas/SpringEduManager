package com.alkemy.springedumanager.repository;

import com.alkemy.springedumanager.model.Practica;
import com.alkemy.springedumanager.model.Practica.EstadoPractica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Repositorio de Practica                                  ║
// ║  Permite consultar prácticas por estudiante, estado       ║
// ║  y curso de forma independiente o combinada.              ║
// ╚═══════════════════════════════════════════════════════════╝
@Repository
public interface PracticaRepository extends JpaRepository<Practica, Long> {

    // Retorna todas las prácticas de un estudiante.
    // SELECT * FROM practicas WHERE estudiante_id = ?
    List<Practica> findByEstudianteId(Long estudianteId);

    // Retorna las prácticas de un estudiante filtradas por estado.
    // SELECT * FROM practicas WHERE estudiante_id = ? AND estado = ?
    List<Practica> findByEstudianteIdAndEstado(Long estudianteId, EstadoPractica estado);

    // Retorna todas las prácticas de un curso (vista admin).
    // SELECT * FROM practicas WHERE curso_id = ?
    List<Practica> findByCursoId(Long cursoId);

    // Retorna las prácticas de un estudiante en un curso específico.
    // SELECT * FROM practicas WHERE estudiante_id = ? AND curso_id = ?
    List<Practica> findByEstudianteIdAndCursoId(Long estudianteId, Long cursoId);
}
