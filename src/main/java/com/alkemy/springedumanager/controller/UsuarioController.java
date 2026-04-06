package com.alkemy.springedumanager.controller;

import com.alkemy.springedumanager.model.Usuario;
import com.alkemy.springedumanager.repository.UsuarioRepository;
import com.alkemy.springedumanager.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Controller MVC de Usuario                                ║
// ║  Gestión completa de usuarios del sistema.                ║
// ║  Permite crear, editar, resetear contraseña y eliminar.   ║
// ║  Solo accesible para ROLE_ADMIN.                          ║
// ╚═══════════════════════════════════════════════════════════╝
@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Lista todos los usuarios ─────────────────────────────
    // Muestra todos los usuarios con su rol, estado y estudiante vinculado.
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuarios/lista";
    }

    // ── Formulario nuevo usuario ─────────────────────────────
    // Muestra el formulario para crear un nuevo usuario.
    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("estudiantes", estudianteRepository.findAll());
        return "usuarios/form";
    }

    // Valida unicidad de username, encripta la contraseña y persiste el usuario.
    @PostMapping("/nuevo")
    public String guardar(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String rol,
                          @RequestParam(required = false) Long estudianteId,
                          RedirectAttributes flash) {

        if (usuarioRepository.findByUsername(username).isPresent()) {
            flash.addFlashAttribute("error", "El username ya está en uso");
            return "redirect:/usuarios/nuevo";
        }

        Usuario usuario = new Usuario(username, passwordEncoder.encode(password), rol);
        if (estudianteId != null) {
            estudianteRepository.findById(estudianteId)
                .ifPresent(usuario::setEstudiante);
        }
        usuarioRepository.save(usuario);
        flash.addFlashAttribute("exito", "Usuario creado correctamente");
        return "redirect:/usuarios";
    }

    // ── Formulario editar usuario ────────────────────────────
    // Carga el formulario de edición con los datos del usuario seleccionado.
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado")));
        model.addAttribute("estudiantes", estudianteRepository.findAll());
        return "usuarios/form";
    }

    // Actualiza datos del usuario incluyendo su vínculo con un estudiante.
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @RequestParam String username,
                             @RequestParam String rol,
                             @RequestParam(defaultValue = "false") boolean activo,
                             @RequestParam(required = false) Long estudianteId,
                             RedirectAttributes flash) {

        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setUsername(username);
        usuario.setRol(rol);
        usuario.setActivo(activo);

        // Actualiza vínculo con estudiante — null si se deja sin vincular.
        if (estudianteId != null) {
            estudianteRepository.findById(estudianteId)
                .ifPresent(usuario::setEstudiante);
        } else {
            usuario.setEstudiante(null);
        }

        usuarioRepository.save(usuario);
        flash.addFlashAttribute("exito", "Usuario actualizado");
        return "redirect:/usuarios";
    }

    // ── Resetear contraseña ──────────────────────────────────
    // Muestra el formulario para asignar una nueva contraseña al usuario.
    @GetMapping("/reset-password/{id}")
    public String formularioReset(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado")));
        return "usuarios/reset-password";
    }

    // Encripta la nueva contraseña con BCrypt y la persiste en la BD.
    @PostMapping("/reset-password/{id}")
    public String resetPassword(@PathVariable Long id,
                                @RequestParam String nuevaPassword,
                                RedirectAttributes flash) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
        flash.addFlashAttribute("exito", "Contraseña actualizada correctamente");
        return "redirect:/usuarios";
    }

    // ── Eliminar usuario ─────────────────────────────────────
    // Desvincular el estudiante antes de eliminar para evitar errores de integridad referencial.
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (usuario.getEstudiante() != null) {
            usuario.setEstudiante(null);
            usuarioRepository.save(usuario);
        }
        usuarioRepository.deleteById(id);
        flash.addFlashAttribute("exito", "Usuario eliminado correctamente");
        return "redirect:/usuarios";
    }
}
