package viaje.servicio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import viaje.dtos.DtoFinViaje;
import viaje.dtos.DtoPago;
import viaje.modelo.Pausa;
import viaje.modelo.Usuarioi;
import viaje.modelo.Viaje;
import viaje.repositorio.RepositorioViaje;
import viaje.dtos.DtoDistancia;
import viaje.dtos.DtoDuracion;
import viaje.dtos.DtoInicioViaje;

@Service
public class ServicioViaje {
	
	@Autowired
	private RepositorioViaje RepositorioViaje;
	@Autowired
	private ServicioAutenticidad authService;
	
	private HttpClient client = HttpClient.newHttpClient();

	public ResponseEntity<Viaje> startRide(HttpServletRequest request, DtoInicioViaje dto) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		Usuarioi user = authService.getUsuario(token);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		if (!authService.canStartRide(token)) {
			return ResponseEntity.badRequest().build();
		}

		String currentStopResponse = httpGet("http://localhost:8083/monopatin/" + dto.getIdMonopatin() + "/currentStop", token);
		if (currentStopResponse != null && !currentStopResponse.isEmpty()) {
			return ResponseEntity.ok(RepositorioViaje.save(convertToEntity(dto, user.getId())));
		}
		return ResponseEntity.badRequest().build();
	}
	

	public ResponseEntity<Viaje> endRide(HttpServletRequest request, int rideId, DtoFinViaje dto) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		Usuarioi user = authService.getUsuario(token);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}


		//Verifica que el viaje exista
		Optional<Viaje> optionalRide = RepositorioViaje.findById(rideId);
		if (!optionalRide.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Viaje ride = optionalRide.get();
		//Verifica que el viaje pertenezca al usuario autenticado
		if (ride.getUserId() != user.getId()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		//Verifica que el viaje no haya finalizado
		if (ride.getFin() != null) {
			return ResponseEntity.badRequest().build();
		}
		
		boolean monoEnParada = monoEnParada(ride.getIdMonopatin(), token);
		if (!monoEnParada) {
			return ResponseEntity.badRequest().build();
		}
		
		//Obtiene las tarifas actuales
		String standardPriceResponse = httpGet("http://localhost:8082/configuracion/currentStandardPrice", token);
		String extendedPausePriceResponse = httpGet("http://localhost:8082/configuracion/currentExtendedPausePrice", token);
		if (standardPriceResponse == null || extendedPausePriceResponse == null) {
			return ResponseEntity.badRequest().build();
		}

		//Inicializa variables
		LocalDateTime fin = LocalDateTime.now();
		
		//Calcula el precio del viaje
		double totalPrice = 0;
		double tarifa = Double.valueOf(standardPriceResponse);
		double tarifa2 = Double.valueOf(extendedPausePriceResponse);
		LocalTime higherRateStartTime = null;
		
		for (Pausa pause : ride.getPauses()) {
			Duration pauseDuration = Duration.between(pause.getInicio(), pause.getFin());
			long pauseSeconds = pauseDuration.getSeconds();
			
			if (pauseSeconds > 900) {
				LocalTime currentHigherRateStartTime = pause.getInicio().plusMinutes(15);
				if (higherRateStartTime == null) {
					higherRateStartTime = currentHigherRateStartTime;
				} else if (currentHigherRateStartTime.isBefore(higherRateStartTime)) {
					higherRateStartTime = currentHigherRateStartTime;
				}
			}
		}
		
		if (higherRateStartTime == null) {
			Duration totalDuration = Duration.between(ride.getInicio(), fin);
			totalPrice += totalDuration.getSeconds() * tarifa;
		} else {
			Duration standardRateTime = Duration.between(ride.getInicio().toLocalTime(), higherRateStartTime);
			totalPrice += standardRateTime.getSeconds() * tarifa;
			Duration higherRateTime = Duration.between(higherRateStartTime, fin);
			totalPrice += higherRateTime.getSeconds() * tarifa2;
		}

		
		//Establece los valores del viaje
		ride.setEndTime(fin);
		ride.setDistancia(dto.getDistancia());
		ride.setPrice(totalPrice);
		
		//Cobra el servicio
		boolean paidService = pagar(totalPrice, token);
		if (!paidService) {
			return ResponseEntity.badRequest().build();
		}
		
		//Guarda los cambios
		return ResponseEntity.ok(RepositorioViaje.save(ride));
	}
	
	private boolean monoEnParada(int idMonopatin, String token)  {
		String currentStopResponse = httpGet("http://localhost:8083/monopatin/" + idMonopatin + "/currentStop", token);
		if (currentStopResponse != null && !currentStopResponse.isEmpty()) {
			return true;
		}
		return false;
	}

	private boolean pagar(double precio, String token) {
		String url = "http://localhost:8081/users/pagar";
		String json = convertToJson(new DtoPago(precio));
		
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
				.header("Authorization", "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .method("POST", HttpRequest.BodyPublishers.ofString(json))
                .build();
        
		try {
		    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		    if (response.statusCode() == 200) {
		    	return true;
		    }
        } catch (Exception e) {
            return false;
        }
		return false;
	}
	
	private Viaje convertToEntity(DtoInicioViaje dto, int userId) {
		return new Viaje(LocalDateTime.now(), userId, dto.getIdMonopatin());
	}
	
	private String convertToJson(DtoPago dto) {
        ObjectMapper objectMapper = new ObjectMapper();
         try {
             return objectMapper.writeValueAsString(dto);
         } catch (JsonProcessingException e) {
             e.printStackTrace();
         }
        return "";
	}

	public ResponseEntity<List<DtoDuracion>> getMonopatinOrderedByTotalTime(HttpServletRequest request) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRol(token);
		if (rol == null || (rol != null && rol.equals("USER"))) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

	    List<Viaje> viaje = RepositorioViaje.findAll();
	    Map<Integer, Long> tiempoTotalMono = new HashMap<>();
	    for (Viaje ride : viaje) {
	    	Integer idMonopatin = ride.getIdMonopatin();
			long rideDurationInSeconds = calculateDurationInSeconds(ride.getInicio().toLocalTime(), ride.getFin().toLocalTime());
			long tiempoMono = tiempoTotalMono.getOrDefault(idMonopatin, 0L);
			tiempoTotalMono.put(ride.getIdMonopatin(), rideDurationInSeconds + tiempoMono);
		}
	    List<DtoDuracion> monopatinDtos = new ArrayList<>();
	    for (Map.Entry<Integer, Long> entry : tiempoTotalMono.entrySet()) {
			monopatinDtos.add(new DtoDuracion(entry.getKey(), entry.getValue()));
		}
	    
	    return ResponseEntity.ok(monopatinDtos);
	}

	public ResponseEntity<List<DtoDuracion>> getMonopatinOrderedByTotalTimeWithoutPauses(HttpServletRequest request) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRol(token);
		if (rol == null || (rol != null && rol.equals("USER"))) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

	    List<Viaje> viaje = RepositorioViaje.findAll();
	    Map<Integer, Long> tiempoTotalMono = new HashMap<>();
	    
	    for (Viaje ride : viaje) {
	    	Integer idMonopatin = ride.getIdMonopatin();
	    	long rideDurationInSeconds = calculateDurationInSeconds(ride.getInicio().toLocalTime(), ride.getFin().toLocalTime());
			long tiempoMono = tiempoTotalMono.getOrDefault(idMonopatin, 0L);
			
			long totalPauseSeconds = 0;
			for (Pausa pause : ride.getPauses()) {
				if (pause.getFin() != null) {
					long pauseDurationInSeconds = calculateDurationInSeconds(pause.getInicio(), pause.getFin());
					totalPauseSeconds += pauseDurationInSeconds;
				}
			}
			
			tiempoTotalMono.put(ride.getIdMonopatin(), tiempoMono + rideDurationInSeconds - totalPauseSeconds);
		}
	    
	    List<DtoDuracion> monopatinDtos = new ArrayList<>();
	    for (Map.Entry<Integer, Long> entry : tiempoTotalMono.entrySet()) {
			monopatinDtos.add(new DtoDuracion(entry.getKey(), entry.getValue()));
		}
	    return ResponseEntity.ok(monopatinDtos);
	}
	
	private long calculateDurationInSeconds(LocalTime inicio, LocalTime fin) {
		Duration duration = Duration.between(inicio, fin);
		return duration.getSeconds();
	}


    public ResponseEntity<List<Viaje>> findAll(HttpServletRequest request) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRol(token);
		if (rol != null && (rol.equals("ADMIN") || rol.equals("Mantenimiento"))) {
			return ResponseEntity.ok(RepositorioViaje.findAll());
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }


    public ResponseEntity<List<DtoDistancia>> getMonopatinOrderedByDistanciaTotal(HttpServletRequest request) {
		String token = authService.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String rol = authService.getRol(token);
		if (rol != null && (rol.equals("ADMIN") || rol.equals("Mantenimiento"))) {
			List<DtoDistancia> monopatin = RepositorioViaje.getMonopatinOrderedByDistanciaTotal();
			return ResponseEntity.ok(monopatin);
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

	public String httpGet(String url, String token) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + token)
            .build();

		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return response.body();
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}