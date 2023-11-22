package cuenta.dtos;

public class DtoRegistro {
	private String nombre;
	private String email;
	private String celular;
	private String clave;
    private String rol;
    
    public DtoRegistro(){}

    public DtoRegistro(String nombre, String email, String celular, String clave, String rol) {
        this.nombre = nombre;
        this.email = email;
        this.celular = celular;
        this.clave = clave;
        this.rol = rol;
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
}