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
import viaje.dtos.DtoPausa;
import viaje.modelo.Pausa;
import viaje.servicio.ServicioPausa;

@RestController
@RequestMapping("/pauses")
public class ControladorPausa {
	
	@Autowired
	private ServicioPausa pausesService;
	
	@PostMapping
	public ResponseEntity<Pausa> startPause(HttpServletRequest request, @RequestBody DtoPausa dto) {
		return pausesService.startPause(request, dto);
	}

	@PatchMapping("{pauseId}/end")
	public ResponseEntity<Pausa> endPause(HttpServletRequest request, @PathVariable int pauseId) {
		return pausesService.endPause(request, pauseId);
	}
	
	@GetMapping
	public ResponseEntity<List<Pausa>> findAll(HttpServletRequest request) {
		return pausesService.findAll(request);
	}
}