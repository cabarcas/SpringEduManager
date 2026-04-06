package com.alkemy.springedumanager.security;

import com.alkemy.springedumanager.repository.UsuarioRepository;
import com.alkemy.springedumanager.model.Usuario;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Puente entre la tabla "usuarios" y Spring Security.      ║
// ║                                                           ║
// ║  Spring Security llama a loadUserByUsername()             ║
// ║  automáticamente cuando alguien envía el form de login.   ║
// ║  Solo retorna el UserDetails — Spring se encarga          ║
// ║  de comparar el password con BCrypt.                      ║
// ╚═══════════════════════════════════════════════════════════╝
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    // Carga el usuario desde la BD y construye el UserDetails
    // que Spring Security usa para verificar credenciales y asignar roles.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() ->
                new UsernameNotFoundException("Usuario no encontrado: " + username));

        return User.builder()
            .username(usuario.getUsername())
            .password(usuario.getPassword())    // ya es hash BCrypt desde la BD
            .authorities(List.of(new SimpleGrantedAuthority(usuario.getRol())))
            .disabled(!usuario.isActivo())
            .build();
    }
}
