package monopatin.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import monopatin.dtos.DtoParada;
import monopatin.modelo.Parada;
import monopatin.servicio.ServicioParada;

@RestController
@RequestMapping("/stops")
public class ControladorParada {
	
	@Autowired
	private ServicioParada stopsService;
	
	@PostMapping
	public ResponseEntity<Parada> create(HttpServletRequest request, @RequestBody DtoParada dto) {
		return stopsService.save(request, dto);
	}
	
	@GetMapping
	public ResponseEntity<List<Parada>> findlAll(HttpServletRequest request) {
		return stopsService.findAll(request);
	}
}