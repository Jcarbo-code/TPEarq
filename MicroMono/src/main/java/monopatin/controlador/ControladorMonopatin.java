package monopatin.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import monopatin.dtos.DtoUbicacion;
import monopatin.modelo.Monopatin;
import monopatin.modelo.Parada;
import monopatin.servicio.ServicioMonopatin;
import monopatin.dtos.DtoMonopatin;
import monopatin.dtos.DtoMonoDistancia;
import monopatin.dtos.DtoMonoDuracion;

@RestController
@RequestMapping("/scooters")
public class ControladorMonopatin {
	@Autowired
	private ServicioMonopatin scootersService;
	
	@PostMapping
	public ResponseEntity<Monopatin> create(HttpServletRequest request, @RequestBody DtoMonopatin dto) {
		return scootersService.save(request, dto);
	}
	
	@GetMapping
	public ResponseEntity<List<Monopatin>> findAll(HttpServletRequest request) {
        return scootersService.findAll(request);
	}
	
	@GetMapping("/{idMonopatin}")
	public ResponseEntity<Monopatin> getById(HttpServletRequest request, @PathVariable int idMonopatin) {
		return scootersService.getById(request, idMonopatin);
	}
	
    @PatchMapping("/{idMonopatin}/startMantenimiento")
    public ResponseEntity<Monopatin> startMantenimiento(HttpServletRequest request, @PathVariable int idMonopatin) {
        return scootersService.startMantenimiento(request, idMonopatin);
    }
    
    @PatchMapping("/{idMonopatin}/finishMantenimiento")
    public ResponseEntity<Monopatin> finishMantenimiento(HttpServletRequest request, @PathVariable int idMonopatin) {
        return scootersService.finishMantenimiento(request, idMonopatin);
    }
    
    @DeleteMapping("/{idMonopatin}")
    public ResponseEntity<String> removeScooter(HttpServletRequest request, @PathVariable int idMonopatin) {
        return scootersService.removeScooter(request, idMonopatin);
    }  
    
    @GetMapping("/{idMonopatin}/currentStop")
    public ResponseEntity<Parada> currentStop(HttpServletRequest request, @PathVariable int idMonopatin) {
    	return scootersService.currentStop(request, idMonopatin);
    }
    
    @PatchMapping("/{idMonopatin}/updateLocation")
    public ResponseEntity<Monopatin> updateLocation(HttpServletRequest request, @PathVariable int idMonopatin, @RequestBody DtoUbicacion location) {
    	return scootersService.updateLocation(request, idMonopatin, location);
    }

    @GetMapping("/orderedByDistance")
    public ResponseEntity<List<DtoMonoDistancia>> getOrderedByDistance(HttpServletRequest request) {
		return scootersService.getOrderedByTotalDistance(request);
    }

    @GetMapping("/orderedByTotalTime/{includePauses}")
    public ResponseEntity<List<DtoMonoDuracion>> getOrderedByTotalTime(HttpServletRequest request, @PathVariable boolean includePauses) {
    	return scootersService.getOrderedByTotalTime(request, includePauses);
    }
}