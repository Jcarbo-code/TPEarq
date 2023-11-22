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
@RequestMapping("/monopatin")
public class ControladorMonopatin {
	@Autowired
	private ServicioMonopatin monopatinService;
	
	@PostMapping
	public ResponseEntity<Monopatin> create(HttpServletRequest request, @RequestBody DtoMonopatin dto) {
		return monopatinService.save(request, dto);
	}
	
	@GetMapping
	public ResponseEntity<List<Monopatin>> findAll(HttpServletRequest request) {
        return monopatinService.findAll(request);
	}
	
	@GetMapping("/{idMonopatin}")
	public ResponseEntity<Monopatin> getById(HttpServletRequest request, @PathVariable int idMonopatin) {
		return monopatinService.getById(request, idMonopatin);
	}
	
    @PatchMapping("/{idMonopatin}/darMantenimiento")
    public ResponseEntity<Monopatin> darMantenimiento(HttpServletRequest request, @PathVariable int idMonopatin) {
        return monopatinService.darMantenimiento(request, idMonopatin);
    }
    
    @PatchMapping("/{idMonopatin}/finishMantenimiento")
    public ResponseEntity<Monopatin> finishMantenimiento(HttpServletRequest request, @PathVariable int idMonopatin) {
        return monopatinService.finishMantenimiento(request, idMonopatin);
    }
    
    @DeleteMapping("/{idMonopatin}")
    public ResponseEntity<String> borrarMonopatin(HttpServletRequest request, @PathVariable int idMonopatin) {
        return monopatinService.borrarMonopatin(request, idMonopatin);
    }  
    
    @GetMapping("/{idMonopatin}/currentStop")
    public ResponseEntity<Parada> currentStop(HttpServletRequest request, @PathVariable int idMonopatin) {
    	return monopatinService.currentStop(request, idMonopatin);
    }
    
    @PatchMapping("/{idMonopatin}/updateLocation")
    public ResponseEntity<Monopatin> updateLocation(HttpServletRequest request, @PathVariable int idMonopatin, @RequestBody DtoUbicacion location) {
    	return monopatinService.updateLocation(request, idMonopatin, location);
    }

    @GetMapping("/orderedByDistance")
    public ResponseEntity<List<DtoMonoDistancia>> getOrderedByDistance(HttpServletRequest request) {
		return monopatinService.getOrderedByDistanciaTotal(request);
    }

    @GetMapping("/orderedByTotalTime/{includePauses}")
    public ResponseEntity<List<DtoMonoDuracion>> getOrderedByTotalTime(HttpServletRequest request, @PathVariable boolean includePauses) {
    	return monopatinService.getOrderedByTotalTime(request, includePauses);
    }
}