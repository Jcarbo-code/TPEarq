package viaje.dtos;

public class DtoDistancia {
	
	private int id;
	private double totalDistance;
	
	public DtoDistancia(int id, double totalDistance) {
		super();
		this.id = id;
		this.totalDistance = totalDistance;
	}
	
	public DtoDistancia() {}

	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	public double getTotalDistance() {return totalDistance;}
	public void setTotalDistance(double totalDistance) {this.totalDistance = totalDistance;}
}
