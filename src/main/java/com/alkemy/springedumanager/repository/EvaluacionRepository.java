package com.alkemy.springedumanager.repository;

import com.alkemy.springedumanager.model.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Repositorio de Evaluacion                                ║
// ║  Incluye queries JPQL con @Query para cálculos            ║
// ║  de promedios directamente en la base de datos.           ║
// ╚═══════════════════════════════════════════════════════════╝
@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    // Retorna todas las evaluaciones de un estudiante.
    // SELECT * FROM evaluaciones WHERE estudiante_id = ?
    List<Evaluacion> findByEstudianteId(Long estudianteId);

    // Retorna las evaluaciones de un estudiante en un curso específico.
    // SELECT * FROM evaluaciones WHERE estudiante_id = ? AND curso_id = ?
    List<Evaluacion> findByEstudianteIdAndCursoId(Long estudianteId, Long cursoId);

    // Retorna todas las evaluaciones de un curso (vista admin).
    // SELECT * FROM evaluaciones WHERE curso_id = ?
    List<Evaluacion> findByCursoId(Long cursoId);

    // Calcula el promedio de notas de un estudiante usando JPQL.
    // SELECT AVG(e.nota) FROM evaluaciones e WHERE e.estudiante_id = ?
    @Query("SELECT AVG(e.nota) FROM Evaluacion e WHERE e.estudiante.id = :estudianteId")
    Double promedioByEstudianteId(@Param("estudianteId") Long estudianteId);

    // Calcula el promedio de notas de un estudiante en un curso específico usando JPQL.
    // SELECT AVG(e.nota) FROM evaluaciones e WHERE e.estudiante_id = ? AND e.curso_id = ?
    @Query("SELECT AVG(e.nota) FROM Evaluacion e WHERE e.estudiante.id = :estudianteId AND e.curso.id = :cursoId")
    Double promedioByEstudianteIdAndCursoId(
        @Param("estudianteId") Long estudianteId,
        @Param("cursoId") Long cursoId
    );
}
