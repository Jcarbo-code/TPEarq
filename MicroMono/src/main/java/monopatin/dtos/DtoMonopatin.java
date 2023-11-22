package monopatin.dtos;

import java.time.LocalDate;

public class DtoMonopatin {

	private String estado;
	private double latitud;
	private double longitud;
	private LocalDate ultimoMantenimiento;
	
	public DtoMonopatin(double latitud, double longitud, LocalDate ultimoMantenimiento) {
		this.estado = "libre";
		this.latitud = latitud;
		this.longitud = longitud;
		this.ultimoMantenimiento = ultimoMantenimiento;
	}
	
	public DtoMonopatin() {}

	public String getEstado() {return estado;}
	public double getLatitud() {return latitud;}
	public double getLongitud() {return longitud;}
	public LocalDate getUltimoMantenimiento() {return ultimoMantenimiento;}
}