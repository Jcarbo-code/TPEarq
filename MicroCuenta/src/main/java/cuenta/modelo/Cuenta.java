package cuenta.modelo;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Cuenta {
	
	@Id @GeneratedValue (strategy = GenerationType.AUTO)
	private int id;
	
	@Column
	private LocalDate fechaAlta;
	
	@Column
	private double saldo;
	
	@Column
	private String mercadoPagoId;
	
	@Column
	private boolean anulada;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "accounts")
	private Set<Usuario> users;
	
	public Cuenta(LocalDate fechaAlta, double saldo, String mercadoPagoId) {
		this.fechaAlta = LocalDate.now();
		this.saldo = saldo;
		this.mercadoPagoId = mercadoPagoId;
		this.anulada = true;
		this.users = new HashSet<>();
	}
	
	public Cuenta() {}
	
	public int getId() {return id;}
	public LocalDate getFechaAlta() {return fechaAlta;}
	public double getSaldo() {return saldo;}
	public String getMercadoPagoId() {return mercadoPagoId;}
	public boolean  getAnulada() {return anulada;}
	public Set<Usuario> getUsers() {return users;}
	
	public void addMoney(double saldo) {
		this.saldo += saldo;
	}
	
	public void addUser(Usuario user) {
		users.add(user);
	}
	
    public void removeUser(Usuario user) {
        users.remove(user);
    }

	public void deactivate() {
		anulada = false;
	}
	
	public void activate() {
		anulada = true;
	}

	public void payService(double saldo) {
		this.saldo -= saldo;
	}
}