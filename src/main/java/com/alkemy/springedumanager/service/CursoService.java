package com.alkemy.springedumanager.service;

import com.alkemy.springedumanager.model.Curso;
import com.alkemy.springedumanager.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Servicio de Curso                                        ║
// ║  Contiene la lógica de negocio relacionada a cursos.      ║
// ║  @Transactional garantiza rollback automático si algo     ║
// ║  falla dentro del método.                                 ║
// ╚═══════════════════════════════════════════════════════════╝
@Service
@RequiredArgsConstructor
@Transactional
public class CursoService {

    private final CursoRepository cursoRepository;

    // Retorna solo los cursos activos — usado en formularios y vistas de selección.
    public List<Curso> listarActivos() {
        return cursoRepository.findByActivoTrue();
    }

    // Retorna todos los cursos sin filtrar — usado en la API REST y vistas admin.
    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    // Busca un curso por id. Lanza excepción si no existe.
    public Curso buscarPorId(Long id) {
        return cursoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado con id: " + id));
    }

    // Persiste un nuevo curso. Solo ADMIN puede llamar a este método (protegido en el controller).
    public Curso guardar(Curso curso) {
        return cursoRepository.save(curso);
    }

    // Actualiza los campos editables de un curso existente.
    public Curso actualizar(Long id, Curso datos) {
        Curso existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setDuracionHoras(datos.getDuracionHoras());
        existente.setActivo(datos.isActivo());
        return cursoRepository.save(existente);
    }

    // Elimina un curso por id.
    public void eliminar(Long id) {
        cursoRepository.deleteById(id);
    }

    // Retorna los cursos en los que está inscrito un estudiante específico.
    @Transactional(readOnly = true)
    public List<Curso> listarPorEstudiante(Long estudianteId) {
        return cursoRepository.findByEstudiantesId(estudianteId);
    }
}
