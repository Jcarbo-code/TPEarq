package cuenta.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cuenta.dtos.DtoAutenticidad;
import cuenta.dtos.DtoLogueo;
import cuenta.dtos.DtoRegistro;
import cuenta.servicio.ServicioAutenticidad;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class ControladorAutenticidad {

    @Autowired
    private ServicioAutenticidad authService;

    @PostMapping("/login")
    public ResponseEntity<DtoAutenticidad> login(@RequestBody DtoLogueo request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public ResponseEntity<DtoAutenticidad> register(@RequestBody DtoRegistro request) {
        return authService.register(request);
    }

    @GetMapping("/isTokenValid")
    public Boolean isTokenValid(HttpServletRequest request) {
        return authService.isTokenValid(request);
    }
}