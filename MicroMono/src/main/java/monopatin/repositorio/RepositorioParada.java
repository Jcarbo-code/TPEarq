package monopatin.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import monopatin.modelo.Parada;

public interface RepositorioParada extends JpaRepository<Parada, Integer> {}