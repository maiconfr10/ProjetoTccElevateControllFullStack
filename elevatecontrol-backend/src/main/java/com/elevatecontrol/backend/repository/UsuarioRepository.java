package com.elevatecontrol.backend.repository;
import com.elevatecontrol.backend.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UsuarioRepository extends JpaRepository<Usuario,String> {
    Optional<Usuario> findByEmailIgnoreCase(String email); boolean existsByEmailIgnoreCase(String email); List<Usuario> findByPerfil(String perfil);
}
