package cuenta.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import cuenta.config.Jwt;
import cuenta.dtos.DtoCuenta;
import cuenta.dtos.DtoPago;
import cuenta.dtos.DtoUsuarioCuenta;
import cuenta.modelo.Cuenta;
import cuenta.modelo.Usuario;
import cuenta.repositorio.RepositorioCuenta;
import cuenta.repositorio.RepositorioUsuario;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class ServicioUsuario {
	
	@Autowired
	private RepositorioUsuario usersRepository;
	@Autowired
	private RepositorioCuenta cuentaRepository;
	@Autowired
	private Jwt jwtAuthenticationFilter;
	@Autowired
	private JWT jwtService;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	AuthenticationManager authManager;

    public ResponseEntity<Usuario> linkNewAccount(HttpServletRequest request, DtoCuenta dto) {
		String token = jwtAuthenticationFilter.getToken(request);
        String email = jwtService.getUsernameFromToken(token);
        Usuario user = usersRepository.findByEmail(email).get();

		Cuenta account = new Cuenta(dto.getFechaAlta(), dto.getSaldo(), dto.getMercadoPagoId());
		cuentaRepository.save(account);
		user.addAccount(account);
		return ResponseEntity.ok(usersRepository.save(user));
    }

	public ResponseEntity<Usuario> linkAccount(HttpServletRequest request, Integer accountId) {
		String token = jwtAuthenticationFilter.getToken(request);
        String email = jwtService.getUsernameFromToken(token);
        Usuario user = usersRepository.findByEmail(email).get();

		Optional<Cuenta> optionalAccount = cuentaRepository.findById(accountId);
		if (!optionalAccount.isPresent())
			return ResponseEntity.notFound().build();
		
		Cuenta account = optionalAccount.get();
		if (user.getCuenta().contains(account))
			return ResponseEntity.badRequest().build();
		user.addAccount(account);
		return ResponseEntity.ok(usersRepository.save(user));
	}

    public ResponseEntity<List<Usuario>> findAll(HttpServletRequest request) {
        String token = jwtAuthenticationFilter.getToken(request);
        String email = jwtService.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		var rol = userDetails.getAuthorities().iterator().next().getAuthority();
		if (rol.equals("ADMIN")) {
			return ResponseEntity.ok(usersRepository.findAll());
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    public ResponseEntity<DtoUsuarioCuenta> getUserByToken(HttpServletRequest request) {
		String token = jwtAuthenticationFilter.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
        String email = jwtService.getUsernameFromToken(token);
		Optional<Usuario> optionalUser = usersRepository.findByEmail(email);
		if (optionalUser.isPresent()) {
			return ResponseEntity.ok(convertToDto(optionalUser.get()));
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private DtoUsuarioCuenta convertToDto(Usuario user) {
		return new DtoUsuarioCuenta(user.getId(), user.getNombre(), user.getEmail(),
		user.getCelular(), user.getPassword(), user.getRol(), user.getCuenta());
	}

	public ResponseEntity<Boolean> canStartRide(HttpServletRequest request) {
        String token = jwtAuthenticationFilter.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
        String email = jwtService.getUsernameFromToken(token);
		Optional<Usuario> optionalUser = usersRepository.findByEmail(email);
		if (!optionalUser.isPresent()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		Usuario user = optionalUser.get();
		for (Cuenta account : user.getCuenta()) {
			if (account.getSaldo() > 0 && account. getAnulada()) {
				return ResponseEntity.ok(true);
			}
		}
		return ResponseEntity.ok(false);
    }

    public ResponseEntity<Cuenta> pagar(HttpServletRequest request, DtoPago dto) {
		String token = jwtAuthenticationFilter.getToken(request);
        String email = jwtService.getUsernameFromToken(token);
		Usuario user = usersRepository.findByEmail(email).get();
		for (Cuenta account : user.getCuenta()) {
			if (account. getAnulada() && account.getSaldo() > 0) {
				account.pagar(dto.getPrice());
				return ResponseEntity.ok(cuentaRepository.save(account));
			}
		}
		return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<String> getRol(HttpServletRequest request) {
        String token = jwtAuthenticationFilter.getToken(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String email = jwtService.getUsernameFromToken(token);
		Usuario user = usersRepository.findByEmail(email).get();
		return ResponseEntity.ok(user.getRol());
    }
}