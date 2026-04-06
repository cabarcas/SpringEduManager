package com.alkemy.springedumanager.service;

import com.alkemy.springedumanager.model.Curso;
import com.alkemy.springedumanager.model.Estudiante;
import com.alkemy.springedumanager.model.PlanEstudio;
import com.alkemy.springedumanager.model.Usuario;
import com.alkemy.springedumanager.repository.CursoRepository;
import com.alkemy.springedumanager.repository.EstudianteRepository;
import com.alkemy.springedumanager.repository.PlanEstudioRepository;
import com.alkemy.springedumanager.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Servicio de Estudiante                                   ║
// ║  Contiene la lógica de negocio de estudiantes.            ║
// ║  @Transactional garantiza rollback automático si algo     ║
// ║  falla dentro del método.                                 ║
// ╚═══════════════════════════════════════════════════════════╝
@Service
@RequiredArgsConstructor
@Transactional
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final PlanEstudioRepository planEstudioRepository;

    // Retorna todos los estudiantes registrados.
    public List<Estudiante> listarTodos() {
        return estudianteRepository.findAll();
    }

    // Busca un estudiante por id. Lanza excepción si no existe.
    public Estudiante buscarPorId(Long id) {
        return estudianteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + id));
    }

    // Persiste un nuevo estudiante validando que el RUT no esté duplicado.
    public Estudiante guardar(Estudiante estudiante) {
        if (estudianteRepository.existsByRut(estudiante.getRut())) {
            throw new IllegalArgumentException("Ya existe un estudiante con ese RUT");
        }
        return estudianteRepository.save(estudiante);
    }

    // Para auto-registro — la validación de RUT se hace antes de llamar a este método.
    public Estudiante guardarSinValidarRut(Estudiante estudiante) {
        return estudianteRepository.save(estudiante);
    }

    // Actualiza todos los campos editables del estudiante incluyendo apoderados.
    // El RUT no se actualiza para mantener unicidad.
    public Estudiante actualizar(Long id, Estudiante datos) {
        Estudiante existente = buscarPorId(id);

        // Datos del estudiante
        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setFechaNacimiento(datos.getFechaNacimiento());
        existente.setDireccion(datos.getDireccion());
        existente.setNivelMineduc(datos.getNivelMineduc());
        existente.setSeccion(datos.getSeccion());
        existente.setActivo(datos.isActivo());

        // Apoderado principal
        existente.setNombreApoderado(datos.getNombreApoderado());
        existente.setVinculoApoderado(datos.getVinculoApoderado());
        existente.setEmailApoderado(datos.getEmailApoderado());
        existente.setTelefonoApoderado(datos.getTelefonoApoderado());
        existente.setApoderadoMismaDireccion(datos.isApoderadoMismaDireccion());
        // Solo guarda la dirección propia si no comparte núcleo con el estudiante.
        existente.setDireccionApoderado(
            datos.isApoderadoMismaDireccion() ? null : datos.getDireccionApoderado()
        );

        // Apoderado suplente
        existente.setNombreApoderadoSuplente(datos.getNombreApoderadoSuplente());
        existente.setVinculoApoderadoSuplente(datos.getVinculoApoderadoSuplente());
        existente.setEmailApoderadoSuplente(datos.getEmailApoderadoSuplente());
        existente.setTelefonoApoderadoSuplente(datos.getTelefonoApoderadoSuplente());
        existente.setSuplenteMismaDireccionEstudiante(datos.isSuplenteMismaDireccionEstudiante());
        existente.setSuplenteMismaDireccionPrincipal(datos.isSuplenteMismaDireccionPrincipal());
        // Solo guarda dirección propia si no comparte con ningún otro núcleo.
        existente.setDireccionApoderadoSuplente(
            (datos.isSuplenteMismaDireccionEstudiante() || datos.isSuplenteMismaDireccionPrincipal())
                ? null : datos.getDireccionApoderadoSuplente()
        );

        return estudianteRepository.save(existente);
    }

    // Elimina un estudiante por id.
    public void eliminar(Long id) {
        estudianteRepository.deleteById(id);
    }

    // Verifica si ya existe un estudiante con el RUT dado.
    public boolean existePorRut(String rut) {
        return estudianteRepository.existsByRut(rut);
    }

    // Busca estudiantes cuyo nombre o apellido contenga el término dado.
    @Transactional(readOnly = true)
    public List<Estudiante> buscar(String termino) {
        return estudianteRepository
            .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(termino, termino);
    }

    // Vincula un usuario a un estudiante para que pueda loguearse y ver sus datos.
    public void vincularUsuario(Long estudianteId, Long usuarioId) {
        Estudiante estudiante = buscarPorId(estudianteId);
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setEstudiante(estudiante);
        usuarioRepository.save(usuario);
    }

    // Asigna el plan completo al estudiante e inscribe automáticamente
    // al estudiante en todos los cursos que componen ese plan.
    public void asignarPlan(Long estudianteId, Long planId) {
        Estudiante estudiante = buscarPorId(estudianteId);
        PlanEstudio plan = planEstudioRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        // Inscribe al estudiante en cada curso del plan.
        for (Curso curso : plan.getCursos()) {
            curso.getEstudiantes().add(estudiante);
            cursoRepository.save(curso);
        }
    }
}
