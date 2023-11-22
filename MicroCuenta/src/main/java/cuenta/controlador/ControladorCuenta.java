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
@RequestMapping("/accounts")
public class ControladorCuenta {

	@Autowired
	RepositorioCuenta accountsRepository;
	@Autowired
	ServicioCuenta accountsService;
	
	@PostMapping("/{accountId}/addMoney/{moneyCount}")
	public ResponseEntity<Cuenta> addMoney(HttpServletRequest request, @PathVariable int accountId, @PathVariable double moneyCount) {
		return accountsService.addMoney(request, accountId, moneyCount);
	}
	
	@PostMapping
	public Cuenta create(@RequestBody DtoCuenta dto) {
		return accountsService.save(dto);
	}
	
	@GetMapping
	public ResponseEntity<List<Cuenta>> findAll(HttpServletRequest request) {
		return accountsService.findAll(request);
	}
	
	@GetMapping("/{accountId}")
	public ResponseEntity<Cuenta> getById(HttpServletRequest request, @PathVariable int accountId) {
		return accountsService.getById(request, accountId);
	}
	
	@PatchMapping("/{accountId}/deactivate")
	public ResponseEntity<Cuenta> deactiveAccount(HttpServletRequest request, @PathVariable int accountId) {
		return accountsService.deactivate(request, accountId);
	}
	
	@PatchMapping("/{accountId}/activate")
	public ResponseEntity<Cuenta> activate(HttpServletRequest request, @PathVariable int accountId) {
		return accountsService.activate(request, accountId);
	}
}