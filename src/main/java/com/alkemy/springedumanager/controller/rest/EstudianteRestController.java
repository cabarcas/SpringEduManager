package com.alkemy.springedumanager.controller.rest;

import com.alkemy.springedumanager.model.Estudiante;
import com.alkemy.springedumanager.service.EstudianteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ╔═══════════════════════════════════════════════════════════╗
// ║  API REST de Estudiantes — Interoperabilidad              ║
// ║                                                           ║
// ║  @RestController = @Controller + @ResponseBody            ║
// ║  Todo lo que retornan los métodos se serializa a JSON     ║
// ║  automáticamente (Jackson está incluido con spring-web).  ║
// ║                                                           ║
// ║  Diferencia clave con @Controller:                        ║
// ║    @Controller     → retorna nombre de template HTML      ║
// ║    @RestController → retorna JSON para clientes           ║
// ║                       (Postman, apps React, móvil, etc)   ║
// ║                                                           ║
// ║  Probar con Postman:                                      ║
// ║    GET    http://localhost:8082/api/estudiantes            ║
// ║    POST   http://localhost:8082/api/estudiantes  (JSON)   ║
// ║    PUT    http://localhost:8082/api/estudiantes/1 (JSON)  ║
// ║    DELETE http://localhost:8082/api/estudiantes/1         ║
// ╚═══════════════════════════════════════════════════════════╝
@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
public class EstudianteRestController {

    private final EstudianteService estudianteService;

    // ── GET /api/estudiantes → lista todos ──────────────────
    // Retorna todos los estudiantes en formato JSON.
    @GetMapping
    public ResponseEntity<List<Estudiante>> listar() {
        return ResponseEntity.ok(estudianteService.listarTodos());
    }

    // ── GET /api/estudiantes/{id} → uno por id ──────────────
    // Retorna un estudiante por id en formato JSON.
    @GetMapping("/{id}")
    public ResponseEntity<Estudiante> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(estudianteService.buscarPorId(id));
    }

    // ── POST /api/estudiantes → crear ───────────────────────
    // Crea un nuevo estudiante. Solo ADMIN. Retorna 201 Created.
    // Body esperado en Postman (raw JSON):
    // { "nombre": "Ana", "apellido": "García", "rut": "12.345.678-9", ... }
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Estudiante> crear(@Valid @RequestBody Estudiante estudiante) {
        Estudiante nuevo = estudianteService.guardar(estudiante);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // ── PUT /api/estudiantes/{id} → actualizar ──────────────
    // Actualiza un estudiante por id. Solo ADMIN.
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Estudiante> actualizar(@PathVariable Long id,
                                                  @Valid @RequestBody Estudiante datos) {
        return ResponseEntity.ok(estudianteService.actualizar(id, datos));
    }

    // ── DELETE /api/estudiantes/{id} → eliminar ─────────────
    // Elimina un estudiante por id. Solo ADMIN. Retorna 204 No Content.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        estudianteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
