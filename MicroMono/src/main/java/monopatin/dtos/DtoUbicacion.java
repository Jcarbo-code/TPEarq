package monopatin.dtos;

public class DtoUbicacion {
	
	private double latitud;
	private double longitud;
	
	public DtoUbicacion(double latitud, double longitud) {
		this.latitud = latitud;
		this.longitud = longitud;
	}
	
	public double getLatitud() {return latitud;}
	public double getLongitud() {return longitud;}
}