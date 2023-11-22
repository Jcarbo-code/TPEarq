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
	private RepositorioMonopatin monopatinRepository;
	@Autowired
	private RepositorioParada stopsRepository;
	@Autowired
	private ServicioAutenticidad authService;
	private HttpClient client = HttpClient.newHttpClient();
	
	public ResponseEntity<Monopatin> save(HttpServletRequest request, DtoMonopatin dto) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRol(token);
		if (rol != null && rol.equals("ADMIN")) {
			return ResponseEntity.ok(monopatinRepository.save(convertToEntity(dto)));
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}
    
	public ResponseEntity<Monopatin> darMantenimiento(HttpServletRequest request, int idMonopatin) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRol(token);
		if (rol == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} else if (!rol.equals("Mantenimiento")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

        Optional<Monopatin> monoPureba = monopatinRepository.findById(idMonopatin);
        if (monoPureba.isPresent()) {
            Monopatin monopatin = monoPureba.get();
            if (monopatin.getEstado().equals("Mantenimiento")) {
        		return ResponseEntity.badRequest().build();
            }
            monopatin.setEstado("Mantenimiento");
            return ResponseEntity.ok(monopatinRepository.save(monopatin));
        }
        return ResponseEntity.notFound().build();
	}
	
	public ResponseEntity<Monopatin> finishMantenimiento(HttpServletRequest request, int idMonopatin) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRol(token);
		if (rol == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} else if (!rol.equals("Mantenimiento")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

        Optional<Monopatin> monoPureba = monopatinRepository.findById(idMonopatin);
        if (monoPureba.isPresent()) {
            Monopatin monopatin = monoPureba.get();
            if (!monopatin.getEstado().equals("Mantenimiento")) {
        		return ResponseEntity.badRequest().build();
            }
            monopatin.setEstado("Libre");
            monopatin.setUltimoMantenimiento(LocalDate.now());
            return ResponseEntity.ok(monopatinRepository.save(monopatin));
        }
        return ResponseEntity.notFound().build();
	}
    
	public ResponseEntity<String> borrarMonopatin(HttpServletRequest request, int idMonopatin) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRol(token);
		if (rol == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} else if (!rol.equals("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
        Optional<Monopatin> monoPureba = monopatinRepository.findById(idMonopatin);
        if (monoPureba.isPresent()) {
            Monopatin monopatin = monoPureba.get();
            if (monopatin.getEstado().equals("in-use")) {
            	return ResponseEntity.badRequest().build();
            }
            monopatinRepository.deleteById(idMonopatin);
            return ResponseEntity.ok("eliminado");
        }
        return ResponseEntity.notFound().build();
	}
	
	private Monopatin convertToEntity(DtoMonopatin dto) {
		return new Monopatin(dto.getLatitud(), dto.getLongitud(), dto.getUltimoMantenimiento());
	}

	public ResponseEntity<Monopatin> getById(HttpServletRequest request, int idMonopatin) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		if (!authService.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Optional<Monopatin> monoPureba = monopatinRepository.findById(idMonopatin);
        if (monoPureba.isPresent()) {
            return ResponseEntity.ok(monoPureba.get());
        }
        return ResponseEntity.notFound().build();
	}

	public ResponseEntity<Parada> currentStop(HttpServletRequest request, int idMonopatin) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		if (!authService.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Optional<Monopatin> monoPureba = monopatinRepository.findById(idMonopatin);
		if (!monoPureba.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Monopatin monopatin = monoPureba.get();
		List<Parada> stops = stopsRepository.findAll();
		for (Parada stop : stops) {
			if (locationIsWithinStop(monopatin, stop)) {
				return ResponseEntity.ok(stop);
			}
		}
		return ResponseEntity.ok(null);
	}

	private boolean locationIsWithinStop(Monopatin monopatin, Parada stop) {
		// The tolerance degrees used roughly equate to 111 meters
		double tolerance = 0.001;
		double latDiff = Math.abs(monopatin.getLatitud() - stop.getLatitud());
		double lonDiff = Math.abs(monopatin.getLongitud() - stop.getLongitud());
		
		return latDiff <= tolerance && lonDiff <= tolerance;
	}

	public ResponseEntity<Monopatin> updateLocation(HttpServletRequest request, int idMonopatin, DtoUbicacion location) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		if (!authService.isTokenValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	
		Optional<Monopatin> monoPureba = monopatinRepository.findById(idMonopatin);
		if (!monoPureba.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Monopatin monopatin = monoPureba.get();
		monopatin.setLatitud(location.getLatitud());
		monopatin.setLongitud(location.getLongitud());
		return ResponseEntity.ok(monopatinRepository.save(monopatin));
	}

	public ResponseEntity<List<Monopatin>> findAll(HttpServletRequest request) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRol(token);
		if (rol != null && (rol.equals("ADMIN") || rol.equals("Mantenimiento"))) {
			return ResponseEntity.ok(monopatinRepository.findAll());
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}

    public ResponseEntity<List<DtoMonoDistancia>> getOrderedByDistanciaTotal(HttpServletRequest request) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRol(token);
		if (rol == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} else if (!rol.equals("Mantenimiento")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		String url = "http://localhost:8080/viaje/monopatinOrderedByDistance";
        HttpRequest monopatinRequest = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + token)
            .build();

		try {
			HttpResponse<String> response = client.send(monopatinRequest, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
                String responseBody = response.body();
                ObjectMapper objectMapper = new ObjectMapper();
 				List<DtoDistancia> distancias = objectMapper.readValue(responseBody, new TypeReference<List<DtoDistancia>>() {});
				List<DtoMonoDistancia> MonoDistancia = new ArrayList<>();

				//Maps the List obtained from viaje service to another List with monopatin data
				for (DtoDistancia dto : distancias) {
					Optional<Monopatin> monoPureba = monopatinRepository.findById(dto.getId());
					if (monoPureba.isPresent()) {
						Monopatin monopatin = monoPureba.get();
						MonoDistancia.add(new DtoMonoDistancia(monopatin.getId(), monopatin.getEstado(), monopatin.getLatitud(),
							monopatin.getLongitud(), monopatin.getUltimoMantenimiento(), dto.getDistanciaTotal()));
					}
				}
				//Add monopatin that have not viaje to return list
				for (Monopatin monopatin : monopatinRepository.findAll()) {
					if (!distancias.stream().anyMatch(s -> s.getId() == monopatin.getId())) {
						MonoDistancia.add(new DtoMonoDistancia(monopatin.getId(), monopatin.getEstado(), monopatin.getLatitud(),
						monopatin.getLongitud(), monopatin.getUltimoMantenimiento(), 0));
					}
				}
				return ResponseEntity.ok(MonoDistancia);
			}
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
    }

    public ResponseEntity<List<DtoMonoDuracion>> getOrderedByTotalTime(HttpServletRequest request, boolean includePauses) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRol(token);
		if (rol == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} else if (!rol.equals("Mantenimiento")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		String url = "http://localhost:8080/viaje/monopatinOrderedByTotalTime/" + includePauses;
        HttpRequest monopatinRequest = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + token)
            .build();

		try {
			HttpResponse<String> response = client.send(monopatinRequest, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
                String responseBody = response.body();
                ObjectMapper objectMapper = new ObjectMapper();
 				List<DtoDuracion> timeDtoList = objectMapper.readValue(responseBody, new TypeReference<List<DtoDuracion>>() {});
				List<DtoMonoDuracion> monopatinWithTime = new ArrayList<>();
				//Maps the List obtained from viaje service to another List with monopatin data
				for (DtoDuracion dto : timeDtoList) {
					Optional<Monopatin> monoPureba = monopatinRepository.findById(dto.getIdMonopatin());
					if (monoPureba.isPresent()) {
						Monopatin monopatin = monoPureba.get();
						monopatinWithTime.add(new DtoMonoDuracion(monopatin.getId(), monopatin.getEstado(), monopatin.getLatitud(),
							monopatin.getLongitud(), monopatin.getUltimoMantenimiento(), dto.getTotalTimeSeconds()));
					}
				}
				//Add monopatin that have not viaje to return list
				for (Monopatin monopatin : monopatinRepository.findAll()) {
					if (!timeDtoList.stream().anyMatch(s -> s.getIdMonopatin() == monopatin.getId())) {
						monopatinWithTime.add(new DtoMonoDuracion(monopatin.getId(), monopatin.getEstado(), monopatin.getLatitud(),
						monopatin.getLongitud(), monopatin.getUltimoMantenimiento(), 0L));
					}
				}
				return ResponseEntity.ok(monopatinWithTime);
			}
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
    }
}