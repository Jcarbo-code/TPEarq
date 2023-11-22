package cuenta.modelo;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
public class Usuario implements UserDetails{

	@Id @GeneratedValue (strategy = GenerationType.AUTO)
	private int id;
	
	@Column
	private String nombre;
	
	@Column(unique = true)
	private String email;
	
	@Column
	private String celular;

	@Column
	private String clave;

	@Column
	String rol;
	
	@ManyToMany
	private Set<Cuenta> accounts;

	public Usuario(String nombre, String email, String clave, String celular, String rol) {
		this.nombre = nombre;
		this.email = email;
		this.clave = clave;
		this.celular = celular;
		this.rol = rol;
		this.accounts = new HashSet<>();
	}
	
	public Usuario() {}
	
	public int getId() {return id;}
	public String getNombre() {return nombre;}
	public String getEmail() {return email;}
	public String getCelular() {return celular;}
	public String getRol() {return rol;}
	public Set<Cuenta> getAccounts () {return accounts;}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(rol));
	}

	@Override
	public String getPassword() {
		return clave;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public void addAccount(Cuenta account) {
		this.accounts.add(account);
	}
}