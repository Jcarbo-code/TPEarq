package viaje.modelo;

import java.util.List;

public class Usuarioi {
    private int id;
    private String nombre;
    private String email;
    private String celular;
    private String clave;
    private String rol;
    private List<Cuenta> accounts;
    
    public Usuarioi(int id, String nombre, String email, String celular, String clave, String rol, List<Cuenta> accounts) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.celular = celular;
        this.clave = clave;
        this.rol = rol;
        this.accounts = accounts;
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

    public List<Cuenta> getAccounts() {
        return accounts;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String celular) {
        this.celular = celular;
    }

    public void setPassword(String clave) {
        this.clave = clave;
    }

    public void setRole(String rol) {
        this.rol = rol;
    }

    public void setAccounts(List<Cuenta> accounts) {
        this.accounts = accounts;
    }       
}