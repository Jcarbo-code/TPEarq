package monopatin.dtos;

public class DtoParada {
	
	private double latitud;
	private double longitud;
	
	public DtoParada(double latitud, double longitud) {
		this.latitud = latitud;
		this.longitud = longitud;
	}
	
	public DtoParada() {}

	public double getLatitud() {return latitud;}
	public double getLongitud() {return longitud;}
}