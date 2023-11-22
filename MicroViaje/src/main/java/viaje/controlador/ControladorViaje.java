package viaje.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import viaje.dtos.DtoFinViaje;
import viaje.dtos.DtoDistancia;
import viaje.dtos.DtoDuracion;
import viaje.dtos.DtoInicioViaje;
import viaje.modelo.Viaje;
import viaje.repositorio.RepositorioViaje;
import viaje.servicio.ServicioViaje;

@RestController
@RequestMapping("/rides")
public class ControladorViaje {
	
	@Autowired
	RepositorioViaje ridesRepository;
	
	@Autowired
	ServicioViaje ridesService;
	
	@PostMapping
	public ResponseEntity<Viaje> startRide(HttpServletRequest request, @RequestBody DtoInicioViaje dto) {
		return ridesService.startRide(request, dto);
	}
	
	@PatchMapping("/{rideId}/end")
	public ResponseEntity<Viaje> endRide(HttpServletRequest request, @PathVariable int rideId, @RequestBody DtoFinViaje dto) {
		return ridesService.endRide(request, rideId, dto);
	}
	
	@GetMapping
	public ResponseEntity<List<Viaje>> findAll(HttpServletRequest request) {
		return ridesService.findAll(request);
	}
	
    @GetMapping("/scootersOrderedByDistance")
    public ResponseEntity<List<DtoDistancia>> getScootersOrderedByDistance(HttpServletRequest request) {
		return ridesService.getScootersOrderedByTotalDistance(request);
    }
    
    @GetMapping("/scootersOrderedByTotalTime/{includePauses}")
    public ResponseEntity<List<DtoDuracion>> getScootersOrderedByTotalTime(HttpServletRequest request, @PathVariable boolean includePauses) {
    	if (includePauses) {
    		return ridesService.getScootersOrderedByTotalTime(request);
    	}
        return ridesService.getScootersOrderedByTotalTimeWithoutPauses(request);
    }
}