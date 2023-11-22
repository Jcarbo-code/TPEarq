package monopatin.modelo;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Monopatin {
	
	@Id @GeneratedValue (strategy = GenerationType.AUTO)
	private int id;
	
	@Column
	private String estado;
	
	@Column
	private double latitud;
	
	@Column
	private double longitud;
	
	@Column
	private LocalDate ultimoMantenimiento;

	public Monopatin(double latitud, double longitud, LocalDate ultimoMantenimiento) {
		this.estado = "Libre";
		this.latitud = latitud;
		this.longitud = longitud;
		this.ultimoMantenimiento = ultimoMantenimiento;
	}
	
	public Monopatin() {}

	public int getId() {return id;}
	public String getEstado() {return estado;}
	public double getLatitud() {return latitud;}
	public double getLongitud() {return longitud;}
	public LocalDate getUltimoMantenimiento() {return ultimoMantenimiento;}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public void setUltimoMantenimiento(LocalDate date) {
		this.ultimoMantenimiento = date;
	}
	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}
	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}
}