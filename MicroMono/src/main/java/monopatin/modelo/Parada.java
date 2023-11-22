package monopatin.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Parada {
	
	@Id @GeneratedValue (strategy = GenerationType.AUTO)
	private int id;
	
	@Column
	private double latitud;
	
	@Column
	private double longitud;

	public Parada(double latitud, double longitud) {
		this.latitud = latitud;
		this.longitud = longitud;
	}
	
	public Parada() {}

	public int getId() {return id;}
	public double getLatitud() {return latitud;}
	public double getLongitud() {return longitud;}
}