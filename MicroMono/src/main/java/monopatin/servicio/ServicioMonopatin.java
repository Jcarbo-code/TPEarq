package monopatin.servicio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import monopatin.dtos.DtoDistancia;
import monopatin.dtos.DtoUbicacion;
import monopatin.modelo.Monopatin;
import monopatin.modelo.Parada;
import monopatin.repositorio.RepositorioMonopatin;
import monopatin.repositorio.RepositorioParada;
import monopatin.dtos.DtoMonopatin;
import monopatin.dtos.DtoMonoDistancia;
import monopatin.dtos.DtoMonoDuracion;
import monopatin.dtos.DtoDuracion;

@Service
public class ServicioMonopatin {
	
	@Autowired
	private RepositorioMonopatin scootersRepository;
	@Autowired
	private RepositorioParada stopsRepository;
	@Autowired
	private ServicioAutenticidad authService;
	private HttpClient client = HttpClient.newHttpClient();
	
	public ResponseEntity<Monopatin> save(HttpServletRequest request, DtoMonopatin dto) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRoleFromToken(token);
		if (rol != null && rol.equals("ADMIN")) {
			return ResponseEntity.ok(scootersRepository.save(convertToEntity(dto)));
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}
    
	public ResponseEntity<Monopatin> startMantenimiento(HttpServletRequest request, int idMonopatin) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRoleFromToken(token);
		if (rol == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} else if (!rol.equals("Mantenimiento")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

        Optional<Monopatin> scooterOptional = scootersRepository.findById(idMonopatin);
        if (scooterOptional.isPresent()) {
            Monopatin scooter = scooterOptional.get();
            if (scooter.getEstado().equals("Mantenimiento")) {
        		return ResponseEntity.badRequest().build();
            }
            scooter.setEstado("Mantenimiento");
            return ResponseEntity.ok(scootersRepository.save(scooter));
        }
        return ResponseEntity.notFound().build();
	}
	
	public ResponseEntity<Monopatin> finishMantenimiento(HttpServletRequest request, int idMonopatin) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRoleFromToken(token);
		if (rol == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} else if (!rol.equals("Mantenimiento")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

        Optional<Monopatin> scooterOptional = scootersRepository.findById(idMonopatin);
        if (scooterOptional.isPresent()) {
            Monopatin scooter = scooterOptional.get();
            if (!scooter.getEstado().equals("Mantenimiento")) {
        		return ResponseEntity.badRequest().build();
            }
            scooter.setEstado("libre");
            scooter.setUltimoMantenimiento(LocalDate.now());
            return ResponseEntity.ok(scootersRepository.save(scooter));
        }
        return ResponseEntity.notFound().build();
	}
    
	public ResponseEntity<String> removeScooter(HttpServletRequest request, int idMonopatin) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRoleFromToken(token);
		if (rol == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} else if (!rol.equals("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
        Optional<Monopatin> scooterOptional = scootersRepository.findById(idMonopatin);
        if (scooterOptional.isPresent()) {
            Monopatin scooter = scooterOptional.get();
            if (scooter.getEstado().equals("in-use")) {
            	return ResponseEntity.badRequest().build();
            }
            scootersRepository.deleteById(idMonopatin);
            return ResponseEntity.ok("Scooter removed successfully");
        }
        return ResponseEntity.notFound().build();
	}
	
	private Monopatin convertToEntity(DtoMonopatin dto) {
		return new Monopatin(dto.getLatitud(), dto.getLongitud(), dto.getUltimoMantenimiento());
	}

	public ResponseEntity<Monopatin> getById(HttpServletRequest request, int idMonopatin) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		if (!authService.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Optional<Monopatin> scooterOptional = scootersRepository.findById(idMonopatin);
        if (scooterOptional.isPresent()) {
            return ResponseEntity.ok(scooterOptional.get());
        }
        return ResponseEntity.notFound().build();
	}

	public ResponseEntity<Parada> currentStop(HttpServletRequest request, int idMonopatin) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		if (!authService.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Optional<Monopatin> optionalScooter = scootersRepository.findById(idMonopatin);
		if (!optionalScooter.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Monopatin scooter = optionalScooter.get();
		List<Parada> stops = stopsRepository.findAll();
		for (Parada stop : stops) {
			if (locationIsWithinStop(scooter, stop)) {
				return ResponseEntity.ok(stop);
			}
		}
		return ResponseEntity.ok(null);
	}

	private boolean locationIsWithinStop(Monopatin scooter, Parada stop) {
		// The tolerance degrees used roughly equate to 111 meters
		double tolerance = 0.001;
		double latDiff = Math.abs(scooter.getLatitud() - stop.getLatitud());
		double lonDiff = Math.abs(scooter.getLongitud() - stop.getLongitud());
		
		return latDiff <= tolerance && lonDiff <= tolerance;
	}

	public ResponseEntity<Monopatin> updateLocation(HttpServletRequest request, int idMonopatin, DtoUbicacion location) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		if (!authService.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	
		Optional<Monopatin> optionalScooter = scootersRepository.findById(idMonopatin);
		if (!optionalScooter.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Monopatin scooter = optionalScooter.get();
		scooter.setLatitud(location.getLatitud());
		scooter.setLongitud(location.getLongitud());
		return ResponseEntity.ok(scootersRepository.save(scooter));
	}

	public ResponseEntity<List<Monopatin>> findAll(HttpServletRequest request) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRoleFromToken(token);
		if (rol != null && (rol.equals("ADMIN") || rol.equals("Mantenimiento"))) {
			return ResponseEntity.ok(scootersRepository.findAll());
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}

    public ResponseEntity<List<DtoMonoDistancia>> getOrderedByTotalDistance(HttpServletRequest request) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRoleFromToken(token);
		if (rol == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} else if (!rol.equals("Mantenimiento")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		String url = "http://localhost:8080/rides/scootersOrderedByDistance";
        HttpRequest scootersRequest = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + token)
            .build();

		try {
			HttpResponse<String> response = client.send(scootersRequest, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
                String responseBody = response.body();
                ObjectMapper objectMapper = new ObjectMapper();
 				List<DtoDistancia> distanceDtoList = objectMapper.readValue(responseBody, new TypeReference<List<DtoDistancia>>() {});
				List<DtoMonoDistancia> scootersWithDistance = new ArrayList<>();

				//Maps the List obtained from Rides service to another List with Scooter data
				for (DtoDistancia dto : distanceDtoList) {
					Optional<Monopatin> optionalScooter = scootersRepository.findById(dto.getId());
					if (optionalScooter.isPresent()) {
						Monopatin scooter = optionalScooter.get();
						scootersWithDistance.add(new DtoMonoDistancia(scooter.getId(), scooter.getEstado(), scooter.getLatitud(),
							scooter.getLongitud(), scooter.getUltimoMantenimiento(), dto.getTotalDistance()));
					}
				}
				//Add Scooters that have not rides to return list
				for (Monopatin scooter : scootersRepository.findAll()) {
					if (!distanceDtoList.stream().anyMatch(s -> s.getId() == scooter.getId())) {
						scootersWithDistance.add(new DtoMonoDistancia(scooter.getId(), scooter.getEstado(), scooter.getLatitud(),
						scooter.getLongitud(), scooter.getUltimoMantenimiento(), 0));
					}
				}
				return ResponseEntity.ok(scootersWithDistance);
			}
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
    }

    public ResponseEntity<List<DtoMonoDuracion>> getOrderedByTotalTime(HttpServletRequest request, boolean includePauses) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRoleFromToken(token);
		if (rol == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} else if (!rol.equals("Mantenimiento")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		String url = "http://localhost:8080/rides/scootersOrderedByTotalTime/" + includePauses;
        HttpRequest scootersRequest = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + token)
            .build();

		try {
			HttpResponse<String> response = client.send(scootersRequest, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
                String responseBody = response.body();
                ObjectMapper objectMapper = new ObjectMapper();
 				List<DtoDuracion> timeDtoList = objectMapper.readValue(responseBody, new TypeReference<List<DtoDuracion>>() {});
				List<DtoMonoDuracion> scootersWithTime = new ArrayList<>();
				//Maps the List obtained from Rides service to another List with Scooter data
				for (DtoDuracion dto : timeDtoList) {
					Optional<Monopatin> optionalScooter = scootersRepository.findById(dto.getIdMonopatin());
					if (optionalScooter.isPresent()) {
						Monopatin scooter = optionalScooter.get();
						scootersWithTime.add(new DtoMonoDuracion(scooter.getId(), scooter.getEstado(), scooter.getLatitud(),
							scooter.getLongitud(), scooter.getUltimoMantenimiento(), dto.getTotalTimeSeconds()));
					}
				}
				//Add Scooters that have not rides to return list
				for (Monopatin scooter : scootersRepository.findAll()) {
					if (!timeDtoList.stream().anyMatch(s -> s.getIdMonopatin() == scooter.getId())) {
						scootersWithTime.add(new DtoMonoDuracion(scooter.getId(), scooter.getEstado(), scooter.getLatitud(),
						scooter.getLongitud(), scooter.getUltimoMantenimiento(), 0L));
					}
				}
				return ResponseEntity.ok(scootersWithTime);
			}
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
    }
}