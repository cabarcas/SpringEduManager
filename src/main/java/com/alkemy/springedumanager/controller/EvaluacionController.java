package com.alkemy.springedumanager.controller;

import com.alkemy.springedumanager.model.Evaluacion;
import com.alkemy.springedumanager.service.CursoService;
import com.alkemy.springedumanager.service.EstudianteService;
import com.alkemy.springedumanager.service.EvaluacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Controller MVC de Evaluacion                             ║
// ║  Gestión de evaluaciones sumativas del estudiante.        ║
// ║  Las evaluaciones siempre tienen nota en escala 1.0-7.0.  ║
// ╚═══════════════════════════════════════════════════════════╝
@Controller
@RequestMapping("/evaluaciones")
@RequiredArgsConstructor
public class EvaluacionController {

    private final EvaluacionService evaluacionService;
    private final CursoService cursoService;
    private final EstudianteService estudianteService;

    // ── Vista ADMIN: todas las evaluaciones ──────────────────
    // Lista todas las evaluaciones registradas en el sistema.
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model) {
        model.addAttribute("evaluaciones", evaluacionService.listarTodas());
        return "evaluaciones/lista";
    }

    // ── Formulario nueva evaluación (ADMIN) ──────────────────
    // Muestra el formulario para registrar una nueva evaluación.
    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioNuevo(Model model) {
        model.addAttribute("evaluacion", new Evaluacion());
        model.addAttribute("cursos", cursoService.listarActivos());
        model.addAttribute("estudiantes", estudianteService.listarTodos());
        model.addAttribute("tipos", Evaluacion.TipoEvaluacion.values());
        return "evaluaciones/form";
    }

    // Valida y persiste la nueva evaluación en la BD.
    @PostMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardar(@Valid @ModelAttribute Evaluacion evaluacion,
                          BindingResult result,
                          Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("cursos", cursoService.listarActivos());
            model.addAttribute("estudiantes", estudianteService.listarTodos());
            model.addAttribute("tipos", Evaluacion.TipoEvaluacion.values());
            return "evaluaciones/form";
        }
        evaluacionService.guardar(evaluacion);
        flash.addFlashAttribute("exito", "Evaluación registrada correctamente");
        return "redirect:/evaluaciones";
    }

    // ── Formulario editar evaluación (ADMIN) ─────────────────
    // Carga el formulario de edición con los datos de la evaluación seleccionada.
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("evaluacion", evaluacionService.buscarPorId(id));
        model.addAttribute("cursos", cursoService.listarActivos());
        model.addAttribute("estudiantes", estudianteService.listarTodos());
        model.addAttribute("tipos", Evaluacion.TipoEvaluacion.values());
        return "evaluaciones/form";
    }

    // Valida y actualiza la evaluación en la BD.
    @PostMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute Evaluacion evaluacion,
                             BindingResult result,
                             Model model,
                             RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("cursos", cursoService.listarActivos());
            model.addAttribute("estudiantes", estudianteService.listarTodos());
            model.addAttribute("tipos", Evaluacion.TipoEvaluacion.values());
            return "evaluaciones/form";
        }
        evaluacionService.actualizar(id, evaluacion);
        flash.addFlashAttribute("exito", "Evaluación actualizada");
        return "redirect:/evaluaciones";
    }

    // Elimina la evaluación por id y redirige a la lista.
    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        evaluacionService.eliminar(id);
        flash.addFlashAttribute("exito", "Evaluación eliminada");
        return "redirect:/evaluaciones";
    }

    // ── Vista ESTUDIANTE: sus propias evaluaciones ────────────
    // Lista las evaluaciones del estudiante logueado con su promedio general.
    @GetMapping("/mis-evaluaciones/{estudianteId}")
    public String misEvaluaciones(@PathVariable Long estudianteId, Model model) {
        model.addAttribute("evaluaciones", evaluacionService.listarPorEstudiante(estudianteId));
        model.addAttribute("promedio", evaluacionService.promedioPorEstudiante(estudianteId));
        model.addAttribute("estudianteId", estudianteId);
        return "evaluaciones/mis-evaluaciones";
    }
}
