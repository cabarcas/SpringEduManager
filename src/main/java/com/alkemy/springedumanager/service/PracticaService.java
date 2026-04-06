package com.alkemy.springedumanager.service;

import com.alkemy.springedumanager.model.Practica;
import com.alkemy.springedumanager.model.Practica.EstadoPractica;
import com.alkemy.springedumanager.repository.PracticaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Servicio de Practica                                     ║
// ║  Gestiona las actividades formativas de los estudiantes.  ║
// ║  Las prácticas tienen nota opcional asignada al corregir. ║
// ╚═══════════════════════════════════════════════════════════╝
@Service
@RequiredArgsConstructor
@Transactional
public class PracticaService {

    private final PracticaRepository practicaRepository;

    // ── Consultas ────────────────────────────────────────────

    // Retorna todas las prácticas registradas.
    @Transactional(readOnly = true)
    public List<Practica> listarTodas() {
        return practicaRepository.findAll();
    }

    // Retorna todas las prácticas de un estudiante específico.
    @Transactional(readOnly = true)
    public List<Practica> listarPorEstudiante(Long estudianteId) {
        return practicaRepository.findByEstudianteId(estudianteId);
    }

    // Retorna las prácticas de un estudiante filtradas por estado.
    @Transactional(readOnly = true)
    public List<Practica> listarPorEstudianteYEstado(Long estudianteId, EstadoPractica estado) {
        return practicaRepository.findByEstudianteIdAndEstado(estudianteId, estado);
    }

    // Retorna todas las prácticas de un curso.
    @Transactional(readOnly = true)
    public List<Practica> listarPorCurso(Long cursoId) {
        return practicaRepository.findByCursoId(cursoId);
    }

    // Busca una práctica por id. Lanza excepción si no existe.
    @Transactional(readOnly = true)
    public Practica buscarPorId(Long id) {
        return practicaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Práctica no encontrada con id: " + id));
    }

    // ── Operaciones CRUD ─────────────────────────────────────

    // Persiste una nueva práctica.
    public Practica guardar(Practica practica) {
        return practicaRepository.save(practica);
    }

    // Actualiza todos los campos editables de una práctica existente.
    public Practica actualizar(Long id, Practica datos) {
        Practica existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setFechaEntrega(datos.getFechaEntrega());
        existente.setEstado(datos.getEstado());
        existente.setNota(datos.getNota());
        existente.setCurso(datos.getCurso());
        existente.setEstudiante(datos.getEstudiante());
        return practicaRepository.save(existente);
    }

    // Elimina una práctica por id.
    public void eliminar(Long id) {
        practicaRepository.deleteById(id);
    }
}
