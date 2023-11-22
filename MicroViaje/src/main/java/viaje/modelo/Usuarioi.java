package viaje.modelo;

import java.util.List;

public class Usuarioi {
    private int id;
    private String nombre;
    private String email;
    private String celular;
    private String clave;
    private String rol;
    private List<Cuenta> cuenta;
    
    public Usuarioi(int id, String nombre, String email, String celular, String clave, String rol, List<Cuenta> cuenta) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.celular = celular;
        this.clave = clave;
        this.rol = rol;
        this.cuenta = cuenta;
    }

    public Usuarioi() {
    }

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

    public List<Cuenta> getCuenta() {
        return cuenta;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public void setPassword(String clave) {
        this.clave = clave;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setCuenta(List<Cuenta> cuenta) {
        this.cuenta = cuenta;
    }       
}