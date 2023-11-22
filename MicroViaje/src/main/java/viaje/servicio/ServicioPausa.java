package viaje.servicio;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import viaje.dtos.DtoPausa;
import viaje.modelo.Pausa;
import viaje.modelo.Usuarioi;
import viaje.modelo.Viaje;
import viaje.repositorio.RepositorioPausa;
import viaje.repositorio.RepositorioViaje;

@Service
public class ServicioPausa {
	
	@Autowired
	private RepositorioPausa pausesRepository;
	@Autowired
	private RepositorioViaje ridesRepository;
	@Autowired
	private ServicioAutenticidad authService;

	public ResponseEntity<Pausa> startPause(HttpServletRequest request, DtoPausa dto) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		Usuarioi user = authService.getUserFromToken(token);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Optional<Viaje> optionalRide = ridesRepository.findById(dto.getRideId());
		if (optionalRide.isPresent()) {
			Viaje ride = optionalRide.get();
			if (ride.getUserId() != user.getId()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			if (ride.getEndTime() != null) {
				return ResponseEntity.badRequest().build();
			}
			Pausa pause = new Pausa(LocalTime.now(), ride);
			return ResponseEntity.ok(pausesRepository.save(pause));
		}
		return ResponseEntity.notFound().build();
	}

	public ResponseEntity<Pausa> endPause(HttpServletRequest request, int pauseId) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		Usuarioi user = authService.getUserFromToken(token);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}


		Optional<Pausa> optionalPause = pausesRepository.findById(pauseId);
		if (optionalPause.isPresent()) {
			Pausa pause = optionalPause.get();
			if (pause.getRide().getUserId() != user.getId()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			pause.setEndTime(LocalTime.now());
			return ResponseEntity.ok(pausesRepository.save(pause));
		}
		return ResponseEntity.notFound().build();
	}

	public ResponseEntity<List<Pausa>> findAll(HttpServletRequest request) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRoleFromToken(token);
		if (rol != null && (rol.equals("ADMIN") || rol.equals("Mantenimiento"))) {
			return ResponseEntity.ok(pausesRepository.findAll());
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}
}