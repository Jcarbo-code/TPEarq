package monopatin.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import monopatin.modelo.Monopatin;

public interface RepositorioMonopatin extends JpaRepository<Monopatin, Integer> {}