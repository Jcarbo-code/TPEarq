package viaje.dtos;

public class DtoFinViaje {
	
	private double distancia;
	
	public DtoFinViaje(double distancia) {
		this.distancia = distancia;
	}
	
	public DtoFinViaje() {}

	public double getDistance() {return distancia;}
}