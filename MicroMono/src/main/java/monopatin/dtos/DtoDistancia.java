package monopatin.dtos;

public class DtoDistancia {
    private Integer id;
	private double totalDistance;
    public DtoDistancia() {
    }
    public DtoDistancia(Integer id, double totalDistance) {
        this.id = id;
        this.totalDistance = totalDistance;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public double getTotalDistance() {
        return totalDistance;
    }
    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }
}