package com.alkemy.springedumanager.controller;

import com.alkemy.springedumanager.model.Practica;
import com.alkemy.springedumanager.service.CursoService;
import com.alkemy.springedumanager.service.EstudianteService;
import com.alkemy.springedumanager.service.PracticaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Controller MVC de Practica                               ║
// ║  Gestión de actividades formativas del estudiante.        ║
// ║  Las prácticas tienen nota opcional — se asigna           ║
// ║  cuando el estado pasa a CORREGIDA.                       ║
// ╚═══════════════════════════════════════════════════════════╝
@Controller
@RequestMapping("/practicas")
@RequiredArgsConstructor
public class PracticaController {

    private final PracticaService practicaService;
    private final CursoService cursoService;
    private final EstudianteService estudianteService;

    // ── Vista ADMIN: todas las prácticas ─────────────────────
    // Lista todas las prácticas registradas en el sistema.
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model) {
        model.addAttribute("practicas", practicaService.listarTodas());
        return "practicas/lista";
    }

    // ── Formulario nueva práctica (ADMIN) ────────────────────
    // Muestra el formulario para registrar una nueva práctica.
    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioNuevo(Model model) {
        model.addAttribute("practica", new Practica());
        model.addAttribute("cursos", cursoService.listarActivos());
        model.addAttribute("estudiantes", estudianteService.listarTodos());
        model.addAttribute("estados", Practica.EstadoPractica.values());
        return "practicas/form";
    }

    // Valida y persiste la nueva práctica en la BD.
    @PostMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardar(@Valid @ModelAttribute Practica practica,
                          BindingResult result,
                          Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("cursos", cursoService.listarActivos());
            model.addAttribute("estudiantes", estudianteService.listarTodos());
            model.addAttribute("estados", Practica.EstadoPractica.values());
            return "practicas/form";
        }
        practicaService.guardar(practica);
        flash.addFlashAttribute("exito", "Práctica creada correctamente");
        return "redirect:/practicas";
    }

    // ── Formulario editar práctica (ADMIN) ───────────────────
    // Carga el formulario de edición con los datos de la práctica seleccionada.
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("practica", practicaService.buscarPorId(id));
        model.addAttribute("cursos", cursoService.listarActivos());
        model.addAttribute("estudiantes", estudianteService.listarTodos());
        model.addAttribute("estados", Practica.EstadoPractica.values());
        return "practicas/form";
    }

    // Valida y actualiza la práctica en la BD.
    @PostMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute Practica practica,
                             BindingResult result,
                             Model model,
                             RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("cursos", cursoService.listarActivos());
            model.addAttribute("estudiantes", estudianteService.listarTodos());
            model.addAttribute("estados", Practica.EstadoPractica.values());
            return "practicas/form";
        }
        practicaService.actualizar(id, practica);
        flash.addFlashAttribute("exito", "Práctica actualizada");
        return "redirect:/practicas";
    }

    // Elimina la práctica por id y redirige a la lista.
    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        practicaService.eliminar(id);
        flash.addFlashAttribute("exito", "Práctica eliminada");
        return "redirect:/practicas";
    }

    // ── Vista ESTUDIANTE: sus propias prácticas ───────────────
    // Lista las prácticas del estudiante logueado, filtradas por su id.
    @GetMapping("/mis-practicas/{estudianteId}")
    public String misPracticas(@PathVariable Long estudianteId, Model model) {
        model.addAttribute("practicas", practicaService.listarPorEstudiante(estudianteId));
        model.addAttribute("estudianteId", estudianteId);
        return "practicas/mis-practicas";
    }
}
