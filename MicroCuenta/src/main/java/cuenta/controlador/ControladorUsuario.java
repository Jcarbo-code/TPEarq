package cuenta.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cuenta.dtos.DtoCuenta;
import cuenta.dtos.DtoPago;
import cuenta.dtos.DtoUsuarioCuenta;
import cuenta.modelo.Cuenta;
import cuenta.modelo.Usuario;
import cuenta.repositorio.RepositorioUsuario;
import cuenta.servicio.ServicioUsuario;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/users")
public class ControladorUsuario {
	
	@Autowired
	RepositorioUsuario usersRepository;
	@Autowired
	ServicioUsuario usersService;
	
	@GetMapping
	public ResponseEntity<List<Usuario>> findAll(HttpServletRequest request) {
		return usersService.findAll(request);
	}

	@PostMapping("/linkNewAccount")
	public ResponseEntity<Usuario> linkNewAccount(HttpServletRequest request, @RequestBody DtoCuenta dto) {
		return usersService.linkNewAccount(request, dto);
	}

	@PostMapping("/linkAccount/{accountId}")
	public ResponseEntity<Usuario> linkAccount(HttpServletRequest request, @PathVariable Integer accountId) {
		return usersService.linkAccount(request, accountId);
	}

	@GetMapping("/byToken")
	public ResponseEntity<DtoUsuarioCuenta> getUserByToken(HttpServletRequest request) {
		return usersService.getUserByToken(request);
	}

	@GetMapping("/canStartRide")
	public ResponseEntity<Boolean> canStartRide(HttpServletRequest request) {
		return usersService.canStartRide(request);
	}

	@PostMapping("/pagar")
	public ResponseEntity<Cuenta> pagar(HttpServletRequest request, @RequestBody DtoPago dto) {
		return usersService.pagar(request, dto);
	}

	@GetMapping("/getRol")
	public ResponseEntity<String> getRol(HttpServletRequest request) {
		return usersService.getRol(request);
	}
}