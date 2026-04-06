package com.alkemy.springedumanager.service;

import com.alkemy.springedumanager.model.Evaluacion;
import com.alkemy.springedumanager.repository.EvaluacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Servicio de Evaluacion                                   ║
// ║  Gestiona las evaluaciones sumativas de los estudiantes.  ║
// ║  Expone cálculos de promedio directamente desde la BD.    ║
// ╚═══════════════════════════════════════════════════════════╝
@Service
@RequiredArgsConstructor
@Transactional
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;

    // ── Consultas ────────────────────────────────────────────

    // Retorna todas las evaluaciones registradas.
    @Transactional(readOnly = true)
    public List<Evaluacion> listarTodas() {
        return evaluacionRepository.findAll();
    }

    // Retorna todas las evaluaciones de un estudiante específico.
    @Transactional(readOnly = true)
    public List<Evaluacion> listarPorEstudiante(Long estudianteId) {
        return evaluacionRepository.findByEstudianteId(estudianteId);
    }

    // Retorna las evaluaciones de un estudiante en un curso específico.
    @Transactional(readOnly = true)
    public List<Evaluacion> listarPorEstudianteYCurso(Long estudianteId, Long cursoId) {
        return evaluacionRepository.findByEstudianteIdAndCursoId(estudianteId, cursoId);
    }

    // Retorna todas las evaluaciones de un curso.
    @Transactional(readOnly = true)
    public List<Evaluacion> listarPorCurso(Long cursoId) {
        return evaluacionRepository.findByCursoId(cursoId);
    }

    // Busca una evaluación por id. Lanza excepción si no existe.
    @Transactional(readOnly = true)
    public Evaluacion buscarPorId(Long id) {
        return evaluacionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Evaluación no encontrada con id: " + id));
    }

    // Calcula el promedio general del estudiante redondeado a 1 decimal.
    // Retorna 0.0 si el estudiante no tiene evaluaciones.
    @Transactional(readOnly = true)
    public double promedioPorEstudiante(Long estudianteId) {
        Double promedio = evaluacionRepository.promedioByEstudianteId(estudianteId);
        return promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0.0;
    }

    // Calcula el promedio del estudiante en un curso específico redondeado a 1 decimal.
    @Transactional(readOnly = true)
    public double promedioPorEstudianteYCurso(Long estudianteId, Long cursoId) {
        Double promedio = evaluacionRepository.promedioByEstudianteIdAndCursoId(estudianteId, cursoId);
        return promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0.0;
    }

    // ── Operaciones CRUD ─────────────────────────────────────

    // Persiste una nueva evaluación.
    public Evaluacion guardar(Evaluacion evaluacion) {
        return evaluacionRepository.save(evaluacion);
    }

    // Actualiza todos los campos editables de una evaluación existente.
    public Evaluacion actualizar(Long id, Evaluacion datos) {
        Evaluacion existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setNota(datos.getNota());
        existente.setFecha(datos.getFecha());
        existente.setTipo(datos.getTipo());
        existente.setCurso(datos.getCurso());
        existente.setEstudiante(datos.getEstudiante());
        return evaluacionRepository.save(existente);
    }

    // Elimina una evaluación por id.
    public void eliminar(Long id) {
        evaluacionRepository.deleteById(id);
    }
}
