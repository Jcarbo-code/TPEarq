package cuenta.dtos;

import java.util.Set;

import cuenta.modelo.Cuenta;

public class DtoUsuarioCuenta {
    private int id;
    private String nombre;
    private String email;
    private String celular;
    private String clave;
    private String rol;
    private Set<Cuenta> cuenta;

    public DtoUsuarioCuenta(int id, String nombre, String email, String celular, String clave, String rol,
            Set<Cuenta> cuenta) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.celular = celular;
        this.clave = clave;
        this.rol = rol;
        this.cuenta = cuenta;
    }

    public DtoUsuarioCuenta() {}

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getCelular() {
        return celular;
    }

    public String getClave() {
        return clave;
    }

    public String getRol() {
        return rol;
    }

    public Set<Cuenta> getcuenta() {
        return cuenta;
    }   
}