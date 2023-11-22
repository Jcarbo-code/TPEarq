package config.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import config.dtos.DtoPrecios;
import config.modelo.Config;
import config.servicio.ServicioConfig;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/fares")
public class ControladorConfig {

	@Autowired
	private ServicioConfig fareService;
	
	@PostMapping
	public ResponseEntity<Config> create(HttpServletRequest request, @RequestBody DtoPrecios dto) {
		return fareService.save(request, dto);
	}
	
	@GetMapping("/currentStandardPrice")
	public ResponseEntity<Double> getCurrentStandardPrice(HttpServletRequest request) {
		return fareService.getCurrentStandardPrice(request);
	}
	
	@GetMapping("/currentExtendedPausePrice")
	public ResponseEntity<Double> getCurrentExtendedPausePrice(HttpServletRequest request) {
		return fareService.getCurrentExtendedPausePrice(request);
	}
    
    @GetMapping
    public ResponseEntity<List<Config>> findAll(HttpServletRequest request) {
		return fareService.findAll(request);
    }
}