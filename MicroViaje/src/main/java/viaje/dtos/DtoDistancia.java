package viaje.dtos;

public class DtoDistancia {
	
	private int id;
	private double DistanciaTotal;
	
	public DtoDistancia(int id, double DistanciaTotal) {
		super();
		this.id = id;
		this.DistanciaTotal = DistanciaTotal;
	}
	
	public DtoDistancia() {}

	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	public double getDistanciaTotal() {return DistanciaTotal;}
	public void setDistanciaTotal(double DistanciaTotal) {this.DistanciaTotal = DistanciaTotal;}
}
