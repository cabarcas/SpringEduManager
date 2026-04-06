package com.alkemy.springedumanager.service;

import com.alkemy.springedumanager.model.Curso;
import com.alkemy.springedumanager.model.PlanEstudio;
import com.alkemy.springedumanager.repository.CursoRepository;
import com.alkemy.springedumanager.repository.PlanEstudioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Servicio de PlanEstudio                                  ║
// ║  Gestiona los planes de estudio y la asignación de        ║
// ║  cursos a cada plan.                                      ║
// ╚═══════════════════════════════════════════════════════════╝
@Service
@RequiredArgsConstructor
@Transactional
public class PlanEstudioService {

    private final PlanEstudioRepository planEstudioRepository;
    private final CursoRepository cursoRepository;

    // Retorna todos los planes de estudio registrados.
    @Transactional(readOnly = true)
    public List<PlanEstudio> listarTodos() {
        return planEstudioRepository.findAll();
    }

    // Retorna solo los planes activos — usado en formularios de selección.
    @Transactional(readOnly = true)
    public List<PlanEstudio> listarActivos() {
        return planEstudioRepository.findByActivoTrue();
    }

    // Busca un plan por id. Lanza excepción si no existe.
    @Transactional(readOnly = true)
    public PlanEstudio buscarPorId(Long id) {
        return planEstudioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Plan de estudio no encontrado con id: " + id));
    }

    // Persiste un nuevo plan de estudio.
    public PlanEstudio guardar(PlanEstudio plan) {
        return planEstudioRepository.save(plan);
    }

    // Actualiza los campos editables de un plan existente.
    public PlanEstudio actualizar(Long id, PlanEstudio datos) {
        PlanEstudio existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setNivelMineduc(datos.getNivelMineduc());
        existente.setAnio(datos.getAnio());
        existente.setDescripcion(datos.getDescripcion());
        existente.setActivo(datos.isActivo());
        return planEstudioRepository.save(existente);
    }

    // Elimina un plan de estudio por id.
    public void eliminar(Long id) {
        planEstudioRepository.deleteById(id);
    }

    // Reemplaza todos los cursos del plan con la lista dada.
    public PlanEstudio actualizarCursos(Long planId, List<Long> cursoIds) {
        PlanEstudio plan = buscarPorId(planId);

        Set<Curso> cursos = cursoIds.stream()
            .map(cid -> cursoRepository.findById(cid)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado: " + cid)))
            .collect(Collectors.toSet());

        plan.getCursos().clear();
        plan.getCursos().addAll(cursos);
        return planEstudioRepository.save(plan);
    }

    // Agrega un curso al plan sin eliminar los ya existentes.
    @Transactional
    public PlanEstudio agregarCurso(Long planId, Long cursoId) {
        PlanEstudio plan = planEstudioRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        Curso curso = cursoRepository.findById(cursoId)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado: " + cursoId));
        plan.getCursos().add(curso);
        return planEstudioRepository.save(plan);
    }

    // Quita un curso del plan sin afectar los demás cursos.
    public PlanEstudio quitarCurso(Long planId, Long cursoId) {
        PlanEstudio plan = buscarPorId(planId);
        plan.getCursos().removeIf(c -> c.getId().equals(cursoId));
        return planEstudioRepository.save(plan);
    }
}
