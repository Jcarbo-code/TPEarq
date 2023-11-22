package viaje.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import viaje.dtos.DtoDistancia;
import viaje.modelo.Viaje;

public interface RepositorioViaje extends JpaRepository<Viaje, Integer> {
    @Query("SELECT NEW rides.dtos.ScooterWithDistanceDto(r.idMonopatin, SUM(r.distancia)) FROM Ride r GROUP BY r.idMonopatin ORDER BY SUM(r.distancia) DESC")
    List<DtoDistancia> getScootersOrderedByTotalDistance();
}