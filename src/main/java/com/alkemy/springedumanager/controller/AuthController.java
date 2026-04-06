package com.alkemy.springedumanager.controller;

import com.alkemy.springedumanager.model.Estudiante;
import com.alkemy.springedumanager.model.Usuario;
import com.alkemy.springedumanager.repository.UsuarioRepository;
import com.alkemy.springedumanager.service.EstudianteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Value;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Rutas de autenticación                                   ║
// ║                                                           ║
// ║  El POST /login lo intercepta Spring Security antes de    ║
// ║  llegar acá. Solo necesitas el @GetMapping para mostrar   ║
// ║  la página del formulario.                                ║
// ╚═══════════════════════════════════════════════════════════╝
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteService estudianteService;
    private final PasswordEncoder passwordEncoder;

    // Puerto actual inyectado desde application.properties, 8080 por defecto.
    @Value("${server.port:8080}")
    private int serverPort;

    // ── Login ────────────────────────────────────────────────
    // Muestra el formulario de login con mensajes de error o logout según corresponda.
    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        if (error != null) model.addAttribute("error", "Credenciales incorrectas");
        if (logout != null) model.addAttribute("msg", "Sesión cerrada correctamente");
        return "auth/login";    // → templates/auth/login.html
    }

    // ── Dashboard ────────────────────────────────────────────
    // Carga el dashboard del usuario autenticado.
    // Si el usuario tiene un estudiante vinculado, pasa sus datos al modelo
    // para que las vistas personalizadas del estudiante funcionen correctamente.
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        String username = auth.getName();
        model.addAttribute("username", username);
        model.addAttribute("serverPort", serverPort);

        // Busca el usuario en BD para obtener su estudiante vinculado.
        usuarioRepository.findByUsername(username).ifPresent(usuario -> {
            Estudiante estudiante = usuario.getEstudiante();
            if (estudiante != null) {
                // Es un USER vinculado a un estudiante.
                model.addAttribute("estudiante", estudiante);
                model.addAttribute("estudianteId", estudiante.getId());
            }
        });

        return "dashboard";    // → templates/dashboard.html
    }

    // ── Registro ─────────────────────────────────────────────
    // Ruta pública — cualquiera puede registrarse sin estar logueado.
    // El estudiante crea su cuenta básica aquí.
    // El ADMIN puede completar el perfil completo desde /estudiantes/editar.
    @GetMapping("/registro")
    public String formularioRegistro() {
        return "auth/registro";    // → templates/auth/registro.html
    }

    // Procesa el formulario de registro, crea el estudiante básico y el usuario vinculado.
    @PostMapping("/registro")
    public String procesarRegistro(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String rut,
            @RequestParam String username,
            @RequestParam String password,
            RedirectAttributes flash) {

        // Verifica que el username no exista.
        if (usuarioRepository.findByUsername(username).isPresent()) {
            flash.addFlashAttribute("error", "El nombre de usuario ya está en uso");
            return "redirect:/registro";
        }

        // Verifica que el RUT no exista.
        if (estudianteService.existePorRut(rut)) {
            flash.addFlashAttribute("error", "Ya existe un estudiante con ese RUT");
            return "redirect:/registro";
        }

        // Crea el estudiante básico con valores por defecto
        // hasta que el ADMIN complete el perfil.
        Estudiante estudiante = new Estudiante();
        estudiante.setNombre(nombre);
        estudiante.setApellido(apellido);
        estudiante.setRut(rut);
        estudiante.setFechaNacimiento(java.time.LocalDate.now());
        estudiante.setNivelMineduc("Sin asignar");
        estudiante.setSeccion("A");
        estudiante.setNombreApoderado("Sin asignar");
        estudiante.setEmailApoderado(username + "@pendiente.cl");
        estudiante.setActivo(true);
        Estudiante savedEstudiante = estudianteService.guardarSinValidarRut(estudiante);

        // Crea el usuario vinculado al estudiante con contraseña hasheada con BCrypt.
        Usuario nuevoUsuario = new Usuario(
            username,
            passwordEncoder.encode(password),
            "ROLE_USER"
        );
        nuevoUsuario.setEstudiante(savedEstudiante);
        usuarioRepository.save(nuevoUsuario);

        flash.addFlashAttribute("msg", "Registro exitoso. Puedes iniciar sesión.");
        return "redirect:/login";
    }
}
