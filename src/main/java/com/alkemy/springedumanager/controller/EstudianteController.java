package com.alkemy.springedumanager.controller;

import com.alkemy.springedumanager.model.Estudiante;
import com.alkemy.springedumanager.repository.UsuarioRepository;
import com.alkemy.springedumanager.service.EstudianteService;
import com.alkemy.springedumanager.service.PlanEstudioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Controller MVC de Estudiante                             ║
// ║  @PreAuthorize protege métodos por rol.                   ║
// ║                                                           ║
// ║  @Controller      → retorna nombres de plantillas HTML    ║
// ║  @RestController  → retorna JSON para clientes externos   ║
// ╚═══════════════════════════════════════════════════════════╝
@Controller
@RequestMapping("/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final UsuarioRepository usuarioRepository;
    private final PlanEstudioService planEstudioService;

    // Muestra la lista de todos los estudiantes en la vista HTML.
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("estudiantes", estudianteService.listarTodos());
        return "estudiantes/lista";   // → templates/estudiantes/lista.html
    }

    // Solo ADMIN accede al formulario de creación de estudiante.
    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioNuevo(Model model) {
        model.addAttribute("estudiante", new Estudiante());
        return "estudiantes/form";
    }

    // Procesa el formulario y persiste el nuevo estudiante en la BD.
    @PostMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardar(@Valid @ModelAttribute Estudiante estudiante,
                          BindingResult result,
                          RedirectAttributes flash) {
        // BindingResult contiene los errores de @NotBlank, @Email, etc.
        if (result.hasErrors()) {
            return "estudiantes/form";  // vuelve al formulario con errores
        }
        try {
            estudianteService.guardar(estudiante);
            flash.addFlashAttribute("exito", "Estudiante registrado correctamente");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/estudiantes";
    }

    // Carga el formulario de edición con los datos del estudiante seleccionado.
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("estudiante", estudianteService.buscarPorId(id));
        return "estudiantes/form";
    }

    // Valida y actualiza el estudiante en la BD.
    @PostMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute Estudiante estudiante,
                             BindingResult result,
                             RedirectAttributes flash) {
        if (result.hasErrors()) return "estudiantes/form";
        estudianteService.actualizar(id, estudiante);
        flash.addFlashAttribute("exito", "Estudiante actualizado");
        return "redirect:/estudiantes";
    }

    // Elimina el estudiante por id y redirige a la lista.
    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        estudianteService.eliminar(id);
        flash.addFlashAttribute("exito", "Estudiante eliminado");
        return "redirect:/estudiantes";
    }

    // Muestra el formulario para vincular un usuario al estudiante.
    @GetMapping("/vincular/{estudianteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioVincular(@PathVariable Long estudianteId, Model model) {
        model.addAttribute("estudiante", estudianteService.buscarPorId(estudianteId));
        model.addAttribute("usuarios", usuarioRepository.findByRol("ROLE_USER"));
        return "estudiantes/vincular";
    }

    // Procesa la vinculación del usuario seleccionado al estudiante.
    @PostMapping("/vincular/{estudianteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String vincular(@PathVariable Long estudianteId,
                        @RequestParam Long usuarioId,
                        RedirectAttributes flash) {
        estudianteService.vincularUsuario(estudianteId, usuarioId);
        flash.addFlashAttribute("exito", "Usuario vinculado correctamente");
        return "redirect:/estudiantes";
    }

    // Muestra el formulario para asignar un plan de estudio al estudiante.
    @GetMapping("/asignar-plan/{estudianteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioAsignarPlan(@PathVariable Long estudianteId, Model model) {
        model.addAttribute("estudiante", estudianteService.buscarPorId(estudianteId));
        model.addAttribute("planes", planEstudioService.listarActivos());
        return "estudiantes/asignar-plan";
    }

    // Procesa la asignación del plan e inscribe al estudiante en todos sus cursos.
    @PostMapping("/asignar-plan/{estudianteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String asignarPlan(@PathVariable Long estudianteId,
             @RequestParam(required = false) Long planId,
             RedirectAttributes flash) {
        if (planId == null) {
            flash.addFlashAttribute("error", "Debes seleccionar un plan de estudio");
            return "redirect:/estudiantes/asignar-plan/" + estudianteId;
        }
        estudianteService.asignarPlan(estudianteId, planId);
        flash.addFlashAttribute("exito", "Plan asignado correctamente");
        return "redirect:/estudiantes";
    }
}
