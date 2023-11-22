package config.servicio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import config.dtos.DtoPrecios;
import config.modelo.Config;
import config.repositorio.RepositorioConfig;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class ServicioConfig {
	
	@Autowired
	private RepositorioConfig fareRepository;
	@Autowired
	private ServicioAutentificacion authService;
	@Autowired
	MongoTemplate mongoTemplate;

	public ResponseEntity<Config> save(HttpServletRequest request, DtoPrecios dto) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRol(token);
		if (rol == null || !rol.equals("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		LocalDate today = LocalDate.now();
		LocalDate fechaCambio = dto.getStartDate();
		if (fechaCambio.isAfter(today) || fechaCambio.isEqual(today)) {
			Config fare = convertToEntity(dto);
			return ResponseEntity.ok(fareRepository.save(fare));
		}
		return ResponseEntity.badRequest().build();
	}
	
	public ResponseEntity<Double> getCurrentStandardPrice(HttpServletRequest request) {
		String token = authService.getTokenFromRequest(request);
		if (token == null || !authService.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

	    LocalDate today = LocalDate.now();
	    Optional<Double> currentStandardPriceOptional = fareRepository.findCurrentStandardPrice(today, mongoTemplate);
	    if (currentStandardPriceOptional.isPresent()) {
	    	return ResponseEntity.ok(currentStandardPriceOptional.get());
	    }
	    return ResponseEntity.notFound().build();
	}
	
	public ResponseEntity<Double> getCurrentExtendedPausePrice(HttpServletRequest request) {
		String token = authService.getTokenFromRequest(request);
		if (token == null || !authService.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

	    LocalDate today = LocalDate.now();
	    Optional<Double> currentExtendedPausePriceOptional = fareRepository.findCurrentExtendedPausePrice(today, mongoTemplate);
	    if (currentExtendedPausePriceOptional.isPresent()) {
	    	return ResponseEntity.ok(currentExtendedPausePriceOptional.get());
	    }
	    return ResponseEntity.notFound().build();
	}

	private Config convertToEntity(DtoPrecios dto) {
		return new Config(dto.getStandardPrice(), dto.getExtendedPausePrice(), dto.getStartDate());
	}

	public ResponseEntity<List<Config>> findAll(HttpServletRequest request) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRol(token);
		if (rol == null || !rol.equals("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.ok(fareRepository.findAll());
	}
}