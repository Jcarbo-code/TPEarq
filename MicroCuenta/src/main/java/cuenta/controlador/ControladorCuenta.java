package cuenta.controlador;

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

import cuenta.dtos.DtoCuenta;
import cuenta.modelo.Cuenta;
import cuenta.repositorio.RepositorioCuenta;
import cuenta.servicio.ServicioCuenta;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/cuenta")
public class ControladorCuenta {

	@Autowired
	RepositorioCuenta cuentaRepository;
	@Autowired
	ServicioCuenta cuentaService;
	
	@PostMapping("/{accountId}/acreditar/{moneyCount}")
	public ResponseEntity<Cuenta> acreditar(HttpServletRequest request, @PathVariable int accountId, @PathVariable double moneyCount) {
		return cuentaService.acreditar(request, accountId, moneyCount);
	}
	
	@PostMapping
	public Cuenta create(@RequestBody DtoCuenta dto) {
		return cuentaService.save(dto);
	}
	
	@GetMapping
	public ResponseEntity<List<Cuenta>> findAll(HttpServletRequest request) {
		return cuentaService.findAll(request);
	}
	
	@GetMapping("/{accountId}")
	public ResponseEntity<Cuenta> getById(HttpServletRequest request, @PathVariable int accountId) {
		return cuentaService.getById(request, accountId);
	}
	
	@PatchMapping("/{accountId}/deactivar")
	public ResponseEntity<Cuenta> deactiveAccount(HttpServletRequest request, @PathVariable int accountId) {
		return cuentaService.deactivar(request, accountId);
	}
	
	@PatchMapping("/{accountId}/activar")
	public ResponseEntity<Cuenta> activar(HttpServletRequest request, @PathVariable int accountId) {
		return cuentaService.activar(request, accountId);
	}
}