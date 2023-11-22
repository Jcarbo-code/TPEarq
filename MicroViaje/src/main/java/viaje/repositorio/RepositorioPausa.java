package viaje.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import viaje.modelo.Pausa;

public interface RepositorioPausa extends JpaRepository<Pausa, Integer> {}