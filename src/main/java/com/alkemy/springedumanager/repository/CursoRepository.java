package com.alkemy.springedumanager.repository;

import com.alkemy.springedumanager.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Repositorio de Curso                                     ║
// ║  JpaRepository provee save(), findById(), findAll(),      ║
// ║  delete(), count() y más sin necesidad de implementación. ║
// ╚═══════════════════════════════════════════════════════════╝
@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    // Retorna solo los cursos marcados como activos.
    // SELECT * FROM cursos WHERE activo = true
    List<Curso> findByActivoTrue();

    // Busca cursos cuyo nombre contenga el texto dado (sin distinción de mayúsculas).
    // SELECT * FROM cursos WHERE LOWER(nombre) LIKE LOWER('%?%')
    List<Curso> findByNombreContainingIgnoreCase(String nombre);

    // Retorna los cursos en los que está inscrito un estudiante específico.
    // Navega la relación ManyToMany: curso → estudiantes → id.
    // SELECT c.* FROM cursos c
    // JOIN curso_estudiantes ce ON c.id = ce.curso_id
    // WHERE ce.estudiante_id = ?
    List<Curso> findByEstudiantesId(Long estudianteId);
}
