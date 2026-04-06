package com.alkemy.springedumanager.repository;

import com.alkemy.springedumanager.model.PlanEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Repositorio de PlanEstudio                               ║
// ║  Permite filtrar planes por estado, nivel MINEDUC y año.  ║
// ╚═══════════════════════════════════════════════════════════╝
@Repository
public interface PlanEstudioRepository extends JpaRepository<PlanEstudio, Long> {

    // Retorna solo los planes marcados como activos.
    // SELECT * FROM planes_estudio WHERE activo = true
    List<PlanEstudio> findByActivoTrue();

    // Retorna todos los planes de un nivel MINEDUC dado.
    // SELECT * FROM planes_estudio WHERE nivel_mineduc = ?
    List<PlanEstudio> findByNivelMineduc(String nivelMineduc);

    // Retorna los planes de un nivel y año específicos.
    // SELECT * FROM planes_estudio WHERE nivel_mineduc = ? AND anio = ?
    List<PlanEstudio> findByNivelMineducAndAnio(String nivelMineduc, Integer anio);
}
