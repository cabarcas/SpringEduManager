package com.alkemy.springedumanager.repository;

import com.alkemy.springedumanager.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Repositorio de Usuario                                   ║
// ║  Spring Security utiliza este repositorio para buscar     ║
// ║  usuarios por username al procesar el formulario login.   ║
// ╚═══════════════════════════════════════════════════════════╝
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca un usuario por su username. Usado por Spring Security al autenticar.
    // SELECT * FROM usuarios WHERE username = ?
    Optional<Usuario> findByUsername(String username);

    // Retorna todos los usuarios con el rol dado.
    // SELECT * FROM usuarios WHERE rol = ?
    List<Usuario> findByRol(String rol);
}
