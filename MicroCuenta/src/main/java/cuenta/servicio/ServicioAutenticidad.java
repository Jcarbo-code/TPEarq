package cuenta.servicio;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cuenta.config.Jwt;
import cuenta.dtos.DtoAutenticidad;
import cuenta.dtos.DtoLogueo;
import cuenta.dtos.DtoRegistro;
import cuenta.modelo.Usuario;
import cuenta.repositorio.RepositorioUsuario;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class ServicioAutenticidad {

    @Autowired
    private RepositorioUsuario usersRepository;
    @Autowired
    private JWT jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private Jwt authenticationFilter;

    public ResponseEntity<DtoAutenticidad> login(DtoLogueo request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserDetails user = usersRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.getToken(user);
        return ResponseEntity.ok(new DtoAutenticidad(token));
    }

    public ResponseEntity<DtoAutenticidad> register(DtoRegistro request) {
        String requestRole = request.getRol();
        if (!requestRole.equals("ADMIN") && !requestRole.equals("USER") && !requestRole.equals("Mantenimiento"))
            return ResponseEntity.badRequest().build();
        Optional<Usuario> optionalUser = usersRepository.findByEmail(request.getEmail());
        if (optionalUser.isPresent()) {
            return ResponseEntity.badRequest().build();
        }


        Usuario user = new Usuario(
            request.getNombre(),
            request.getEmail(),
            passwordEncoder.encode(request.getClave()),
            request.getCelular(),
            requestRole
        );
        usersRepository.save(user);
        return ResponseEntity.ok(new DtoAutenticidad(jwtService.getToken(user)));   
    }

    public Boolean isTokenValid(HttpServletRequest request) {
        String token = authenticationFilter.getToken(request);
        if (token == null)
            return false;
        try {
            String email = jwtService.getUsernameFromToken(token);
            Optional<Usuario> optionalUser = usersRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                return true;
            }
            return false;
        } catch(Exception e) {
            return false;
        }
    }
}