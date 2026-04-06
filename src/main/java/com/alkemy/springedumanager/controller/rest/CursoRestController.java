package com.alkemy.springedumanager.controller.rest;

import com.alkemy.springedumanager.model.Curso;
import com.alkemy.springedumanager.service.CursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ╔═══════════════════════════════════════════════════════════╗
// ║  API REST de Cursos                                       ║
// ║                                                           ║
// ║  Colección Postman sugerida:                              ║
// ║  1. GET    /api/cursos       → ver todos los cursos       ║
// ║  2. GET    /api/cursos/1     → ver curso por id           ║
// ║  3. POST   /api/cursos       → crear (solo ADMIN)         ║
// ║  4. PUT    /api/cursos/1     → editar (solo ADMIN)        ║
// ║  5. DELETE /api/cursos/1     → eliminar (solo ADMIN)      ║
// ║                                                           ║
// ║  Para autenticarse en Postman:                            ║
// ║  Tab Authorization → Basic Auth                          ║
// ║  Username: admin  Password: admin123                      ║
// ╚═══════════════════════════════════════════════════════════╝
@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoRestController {

    private final CursoService cursoService;

    // Retorna todos los cursos en formato JSON.
    @GetMapping
    public ResponseEntity<List<Curso>> listar() {
        return ResponseEntity.ok(cursoService.listarTodos());
    }

    // Retorna un curso por id en formato JSON.
    @GetMapping("/{id}")
    public ResponseEntity<Curso> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.buscarPorId(id));
    }

    // Crea un nuevo curso. Solo ADMIN. Retorna 201 Created.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Curso> crear(@Valid @RequestBody Curso curso) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.guardar(curso));
    }

    // Actualiza un curso por id. Solo ADMIN.
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Curso> actualizar(@PathVariable Long id,
                                             @Valid @RequestBody Curso datos) {
        return ResponseEntity.ok(cursoService.actualizar(id, datos));
    }

    // Elimina un curso por id. Solo ADMIN. Retorna 204 No Content.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
