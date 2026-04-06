package com.alkemy.springedumanager.repository;

import com.alkemy.springedumanager.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Repositorio de Estudiante                                ║
// ║  Esta interfaz reemplaza todo el DAO con JDBC manual.     ║
// ║  JpaRepository ya incluye gratis:                         ║
// ║    save(), findById(), findAll(), delete(), count()...    ║
// ║  Los métodos de abajo son generados por Spring Data       ║
// ║  leyendo el nombre: findBy + NombreDelCampo               ║
// ╚═══════════════════════════════════════════════════════════╝
@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    // Busca un estudiante por su RUT único.
    // SELECT * FROM estudiantes WHERE rut = ?
    Optional<Estudiante> findByRut(String rut);

    // Verifica si existe un estudiante con el RUT dado.
    // SELECT COUNT(*) > 0 FROM estudiantes WHERE rut = ?
    boolean existsByRut(String rut);

    // Busca estudiantes cuyo nombre o apellido contenga el texto dado.
    // SELECT * FROM estudiantes WHERE LOWER(nombre) LIKE LOWER('%?%') OR LOWER(apellido) LIKE LOWER('%?%')
    List<Estudiante> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
        String nombre, String apellido
    );
}
