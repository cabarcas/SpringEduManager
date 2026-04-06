package com.alkemy.springedumanager.controller;

import com.alkemy.springedumanager.model.PlanEstudio;
import com.alkemy.springedumanager.service.CursoService;
import com.alkemy.springedumanager.service.PlanEstudioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Controller MVC de Plan de Estudio                        ║
// ║  Un plan agrupa los cursos de un nivel MINEDUC.           ║
// ║  Al asignar un plan a un estudiante queda inscrito        ║
// ║  automáticamente en todos los cursos del plan.            ║
// ╚═══════════════════════════════════════════════════════════╝
@Controller
@RequestMapping("/planes")
@RequiredArgsConstructor
public class PlanEstudioController {

    private final PlanEstudioService planEstudioService;
    private final CursoService cursoService;

    // Lista todos los planes de estudio registrados.
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model) {
        model.addAttribute("planes", planEstudioService.listarTodos());
        return "planes/lista";
    }

    // Muestra el formulario para crear un nuevo plan de estudio.
    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioNuevo(Model model) {
        model.addAttribute("plan", new PlanEstudio());
        model.addAttribute("nivelesMineduc", nivelesDisponibles());
        return "planes/form";
    }

    // Valida y persiste el nuevo plan de estudio en la BD.
    @PostMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardar(@Valid @ModelAttribute("plan") PlanEstudio plan,
                          BindingResult result,
                          Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("nivelesMineduc", nivelesDisponibles());
            return "planes/form";
        }
        planEstudioService.guardar(plan);
        flash.addFlashAttribute("exito", "Plan de estudio creado correctamente");
        return "redirect:/planes";
    }

    // Carga el formulario de edición con los datos del plan seleccionado.
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("plan", planEstudioService.buscarPorId(id));
        model.addAttribute("nivelesMineduc", nivelesDisponibles());
        return "planes/form";
    }

    // Valida y actualiza el plan de estudio en la BD.
    @PostMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("plan") PlanEstudio plan,
                             BindingResult result,
                             Model model,
                             RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("nivelesMineduc", nivelesDisponibles());
            return "planes/form";
        }
        planEstudioService.actualizar(id, plan);
        flash.addFlashAttribute("exito", "Plan de estudio actualizado");
        return "redirect:/planes";
    }

    // Vista para gestionar qué cursos tiene el plan.
    @GetMapping("/cursos/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String gestionarCursos(@PathVariable Long id, Model model) {
        model.addAttribute("plan", planEstudioService.buscarPorId(id));
        model.addAttribute("todosCursos", cursoService.listarActivos());
        return "planes/cursos";
    }

    // Agrega un curso al plan sin eliminar los ya existentes.
    @PostMapping("/cursos/{planId}/agregar/{cursoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String agregarCurso(@PathVariable Long planId,
                               @PathVariable Long cursoId,
                               RedirectAttributes flash) {
        planEstudioService.agregarCurso(planId, cursoId);
        flash.addFlashAttribute("exito", "Curso agregado al plan");
        return "redirect:/planes/cursos/" + planId;
    }

    // Quita un curso del plan sin afectar los demás cursos.
    @PostMapping("/cursos/{planId}/quitar/{cursoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String quitarCurso(@PathVariable Long planId,
                              @PathVariable Long cursoId,
                              RedirectAttributes flash) {
        planEstudioService.quitarCurso(planId, cursoId);
        flash.addFlashAttribute("exito", "Curso removido del plan");
        return "redirect:/planes/cursos/" + planId;
    }

    // Elimina el plan de estudio por id y redirige a la lista.
    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        planEstudioService.eliminar(id);
        flash.addFlashAttribute("exito", "Plan de estudio eliminado");
        return "redirect:/planes";
    }

    // Retorna la lista de niveles MINEDUC disponibles para los selectores del formulario.
    private List<String> nivelesDisponibles() {
        return List.of(
            "1° Básico", "2° Básico", "3° Básico", "4° Básico",
            "5° Básico", "6° Básico", "7° Básico", "8° Básico",
            "1° Medio", "2° Medio", "3° Medio", "4° Medio"
        );
    }
}
