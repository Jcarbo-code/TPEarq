package cuenta.dtos;

public class DtoUsuario {
	
	private String nombre;
	private String email;
	private String celular;
	private String rol;
	
	public DtoUsuario(String nombre, String email, String celular, String rol) {
		super();
		this.nombre = nombre;
		this.email = email;
		this.celular = celular;
		this.rol = rol;
	}

	public DtoUsuario() {}

	public String getNombre() {return nombre;}
	public String getEmail() {return email;}
	public String getCelular() {return celular;}
	public String getRol() {return rol;}
}