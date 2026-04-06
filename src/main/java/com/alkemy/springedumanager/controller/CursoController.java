package com.alkemy.springedumanager.controller;

import com.alkemy.springedumanager.model.Curso;
import com.alkemy.springedumanager.service.CursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Controller MVC de Curso                                  ║
// ║  Carga de datos protegida por ROLE_ADMIN.                 ║
// ║  Solo los usuarios con rol ADMIN pueden ingresar          ║
// ║  nuevos cursos.                                           ║
// ╚═══════════════════════════════════════════════════════════╝
@Controller
@RequestMapping("/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    // ROLE_USER y ROLE_ADMIN pueden ver la lista de cursos.
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("cursos", cursoService.listarActivos());
        return "cursos/lista";       // → templates/cursos/lista.html
    }

    // Solo ADMIN accede al formulario de nuevo curso.
    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioNuevo(Model model) {
        model.addAttribute("curso", new Curso());
        return "cursos/form";        // → templates/cursos/form.html
    }

    // Valida y persiste el nuevo curso en la BD.
    @PostMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardar(@Valid @ModelAttribute Curso curso,
                          BindingResult result,
                          RedirectAttributes flash) {
        if (result.hasErrors()) return "cursos/form";
        cursoService.guardar(curso);
        flash.addFlashAttribute("exito", "Curso creado correctamente");
        return "redirect:/cursos";
    }

    // Carga el formulario de edición con los datos del curso seleccionado.
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("curso", cursoService.buscarPorId(id));
        return "cursos/form";
    }

    // Valida y actualiza el curso en la BD.
    @PostMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute Curso curso,
                             BindingResult result,
                             RedirectAttributes flash) {
        if (result.hasErrors()) return "cursos/form";
        cursoService.actualizar(id, curso);
        flash.addFlashAttribute("exito", "Curso actualizado");
        return "redirect:/cursos";
    }

    // Elimina el curso por id y redirige a la lista.
    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        cursoService.eliminar(id);
        flash.addFlashAttribute("exito", "Curso eliminado");
        return "redirect:/cursos";
    }

    // Vista exclusiva del estudiante logueado — sus cursos inscritos.
    @GetMapping("/mis-cursos/{estudianteId}")
    public String misCursos(@PathVariable Long estudianteId, Model model) {
        model.addAttribute("cursos", cursoService.listarPorEstudiante(estudianteId));
        return "cursos/mis-cursos";
    }
}
