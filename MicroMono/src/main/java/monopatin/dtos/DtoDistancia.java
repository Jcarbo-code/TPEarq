package monopatin.dtos;

public class DtoDistancia {
    private Integer id;
	private double DistanciaTotal;
    public DtoDistancia() {
    }
    public DtoDistancia(Integer id, double DistanciaTotal) {
        this.id = id;
        this.DistanciaTotal = DistanciaTotal;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public double getDistanciaTotal() {
        return DistanciaTotal;
    }
    public void setDistanciaTotal(double DistanciaTotal) {
        this.DistanciaTotal = DistanciaTotal;
    }
}