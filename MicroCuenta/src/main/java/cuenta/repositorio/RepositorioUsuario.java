package cuenta.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cuenta.modelo.Usuario;

public interface RepositorioUsuario extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
}