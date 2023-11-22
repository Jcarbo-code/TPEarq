package cuenta.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import cuenta.modelo.Cuenta;

public interface RepositorioCuenta extends JpaRepository<Cuenta, Integer> {}