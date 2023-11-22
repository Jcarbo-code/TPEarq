package cuenta.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import cuenta.config.Jwt;
import cuenta.dtos.DtoCuenta;
import cuenta.modelo.Cuenta;
import cuenta.modelo.Usuario;
import cuenta.repositorio.RepositorioCuenta;
import cuenta.repositorio.RepositorioUsuario;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class ServicioCuenta {

	@Autowired
	private RepositorioCuenta cuentaRepository;
	@Autowired
	private RepositorioUsuario usersRepository;
	@Autowired
	private Jwt jwtAuthenticationFilter;
	@Autowired
	private JWT jwtService;
	@Autowired
	private UserDetailsService userDetailsService;
	
	public Cuenta save(DtoCuenta dto) {
		return cuentaRepository.save(convertToEntity(dto));
	}
	
	private Cuenta convertToEntity(DtoCuenta dto) {
		return new Cuenta(dto.getFechaAlta(), dto.getSaldo(), dto.getMercadoPagoId());
	}

	public ResponseEntity<Cuenta> acreditar(HttpServletRequest request, int accountId, double moneyCount) {
		Optional<Cuenta> optionalAccount = cuentaRepository.findById(accountId);
		if (!optionalAccount.isPresent())
			return ResponseEntity.notFound().build();
		Cuenta account = optionalAccount.get();

		String token = jwtAuthenticationFilter.getToken(request);
        String email = jwtService.getUsernameFromToken(token);
        Usuario user = usersRepository.findByEmail(email).get();

		if (!user.getCuenta().contains(account))
			return ResponseEntity.badRequest().build();
		
		/*	transaccion con MP */
		account.acreditar(moneyCount);
		return ResponseEntity.ok(cuentaRepository.save(account));
	}

	public Cuenta linkUser(int accountId, int userId) {
		Optional<Cuenta> optionalAccount = cuentaRepository.findById(accountId);
		if (optionalAccount.isPresent()) {
			Cuenta account = optionalAccount.get();
			Optional<Usuario> optionalUser = usersRepository.findById(userId);
			if (optionalUser.isPresent()) {
				Usuario user = optionalUser.get();
				account.agregarUsuario(user);
				return cuentaRepository.save(account);
			}
			return null;
		}
		return null;
	}

	public ResponseEntity<Cuenta> deactivar(HttpServletRequest request, int id) {
		String token = jwtAuthenticationFilter.getToken(request);
        String email = jwtService.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		var rol = userDetails.getAuthorities().iterator().next().getAuthority();
		if (!rol.equals("ADMIN"))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

		Optional<Cuenta> optionalAccount = cuentaRepository.findById(id);
		if (optionalAccount.isPresent()) {
			Cuenta account = optionalAccount.get();
			account.deactivar();
			return ResponseEntity.ok(cuentaRepository.save(account));
		}
		return ResponseEntity.notFound().build();
	}
	
	public ResponseEntity<Cuenta> activar(HttpServletRequest request, int id) {
		String token = jwtAuthenticationFilter.getToken(request);
        String email = jwtService.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		var rol = userDetails.getAuthorities().iterator().next().getAuthority();
		if (!rol.equals("ADMIN"))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

		Optional<Cuenta> optionalAccount = cuentaRepository.findById(id);
		if (optionalAccount.isPresent()) {
			Cuenta account = optionalAccount.get();
			account.activar();
			return ResponseEntity.ok(cuentaRepository.save(account));
		}
		return ResponseEntity.notFound().build();
	}

	public ResponseEntity<Cuenta> getById(HttpServletRequest request, int accountId) {
		String token = jwtAuthenticationFilter.getToken(request);
        String email = jwtService.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		var rol = userDetails.getAuthorities().iterator().next().getAuthority();
		Usuario user = usersRepository.findByEmail(email).get();

		Optional<Cuenta> optionalAccount = cuentaRepository.findById(accountId);
		if (optionalAccount.isPresent()) {
			Cuenta account = optionalAccount.get();
			if (!rol.equals("ADMIN") && !user.getCuenta().contains(account)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			return ResponseEntity.ok(account);
		}
		return ResponseEntity.notFound().build();
	}

    public ResponseEntity<List<Cuenta>> findAll(HttpServletRequest request) {
		String token = jwtAuthenticationFilter.getToken(request);
        String email = jwtService.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		var rol = userDetails.getAuthorities().iterator().next().getAuthority();
		if (rol.equals("ADMIN")) {
			return ResponseEntity.ok(cuentaRepository.findAll());
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }	
}