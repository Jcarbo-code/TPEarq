package monopatin.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import monopatin.dtos.DtoParada;
import monopatin.modelo.Parada;
import monopatin.repositorio.RepositorioParada;

@Service
public class ServicioParada {
	
	@Autowired
	private RepositorioParada stopsRepository;
	@Autowired
	private ServicioAutenticidad authService;
	
	public ResponseEntity<Parada> save(HttpServletRequest request, DtoParada dto) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRol(token);
		if (rol == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		if (!rol.equals("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();	
		}
		return ResponseEntity.ok(stopsRepository.save(convertToEntity(dto)));
	}
	
	private Parada convertToEntity(DtoParada dto) {
		return new Parada(dto.getLatitud(), dto.getLongitud());
	}

	public ResponseEntity<List<Parada>> findAll(HttpServletRequest request) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRol(token);
		if (rol == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		if (!rol.equals("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();	
		}
		return ResponseEntity.ok(stopsRepository.findAll());
	}
}