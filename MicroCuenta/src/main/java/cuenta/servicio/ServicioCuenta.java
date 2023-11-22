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
	private RepositorioCuenta accountsRepository;
	@Autowired
	private RepositorioUsuario usersRepository;
	@Autowired
	private Jwt jwtAuthenticationFilter;
	@Autowired
	private JWT jwtService;
	@Autowired
	private UserDetailsService userDetailsService;
	
	public Cuenta save(DtoCuenta dto) {
		return accountsRepository.save(convertToEntity(dto));
	}
	
	private Cuenta convertToEntity(DtoCuenta dto) {
		return new Cuenta(dto.getFechaAlta(), dto.getSaldo(), dto.getMercadoPagoId());
	}

	public ResponseEntity<Cuenta> addMoney(HttpServletRequest request, int accountId, double moneyCount) {
		Optional<Cuenta> optionalAccount = accountsRepository.findById(accountId);
		if (!optionalAccount.isPresent())
			return ResponseEntity.notFound().build();
		Cuenta account = optionalAccount.get();

		String token = jwtAuthenticationFilter.getTokenFromRequest(request);
        String email = jwtService.getUsernameFromToken(token);
        Usuario user = usersRepository.findByEmail(email).get();

		if (!user.getAccounts().contains(account))
			return ResponseEntity.badRequest().build();
		
		/*
			* Antes de sumar el dinero la app debería comunicarse con una API de MercadoPago, para
			* restar el dinero de MercadoPago antes de sumarlo aquí (luego de verificar que la cuenta
			* de MercadoPago tenga ese dinero disponible)
			* */
		account.addMoney(moneyCount);
		return ResponseEntity.ok(accountsRepository.save(account));
	}

	public Cuenta linkUser(int accountId, int userId) {
		Optional<Cuenta> optionalAccount = accountsRepository.findById(accountId);
		if (optionalAccount.isPresent()) {
			Cuenta account = optionalAccount.get();
			Optional<Usuario> optionalUser = usersRepository.findById(userId);
			if (optionalUser.isPresent()) {
				Usuario user = optionalUser.get();
				account.addUser(user);
				return accountsRepository.save(account);
			}
			return null;
		}
		return null;
	}

	public ResponseEntity<Cuenta> deactivate(HttpServletRequest request, int id) {
		String token = jwtAuthenticationFilter.getTokenFromRequest(request);
        String email = jwtService.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		var rol = userDetails.getAuthorities().iterator().next().getAuthority();
		if (!rol.equals("ADMIN"))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

		Optional<Cuenta> optionalAccount = accountsRepository.findById(id);
		if (optionalAccount.isPresent()) {
			Cuenta account = optionalAccount.get();
			account.deactivate();
			return ResponseEntity.ok(accountsRepository.save(account));
		}
		return ResponseEntity.notFound().build();
	}
	
	public ResponseEntity<Cuenta> activate(HttpServletRequest request, int id) {
		String token = jwtAuthenticationFilter.getTokenFromRequest(request);
        String email = jwtService.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		var rol = userDetails.getAuthorities().iterator().next().getAuthority();
		if (!rol.equals("ADMIN"))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

		Optional<Cuenta> optionalAccount = accountsRepository.findById(id);
		if (optionalAccount.isPresent()) {
			Cuenta account = optionalAccount.get();
			account.activate();
			return ResponseEntity.ok(accountsRepository.save(account));
		}
		return ResponseEntity.notFound().build();
	}

	public ResponseEntity<Cuenta> getById(HttpServletRequest request, int accountId) {
		String token = jwtAuthenticationFilter.getTokenFromRequest(request);
        String email = jwtService.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		var rol = userDetails.getAuthorities().iterator().next().getAuthority();
		Usuario user = usersRepository.findByEmail(email).get();

		Optional<Cuenta> optionalAccount = accountsRepository.findById(accountId);
		if (optionalAccount.isPresent()) {
			Cuenta account = optionalAccount.get();
			if (!rol.equals("ADMIN") && !user.getAccounts().contains(account)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			return ResponseEntity.ok(account);
		}
		return ResponseEntity.notFound().build();
	}

    public ResponseEntity<List<Cuenta>> findAll(HttpServletRequest request) {
		String token = jwtAuthenticationFilter.getTokenFromRequest(request);
        String email = jwtService.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		var rol = userDetails.getAuthorities().iterator().next().getAuthority();
		if (rol.equals("ADMIN")) {
			return ResponseEntity.ok(accountsRepository.findAll());
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }	
}