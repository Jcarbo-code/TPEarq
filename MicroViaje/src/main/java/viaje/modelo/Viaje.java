package viaje.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Viaje {
	
	@Id @GeneratedValue (strategy = GenerationType.AUTO)
	private int id;
	
	@Column
	private LocalDateTime inicio;
	
	@Column
	private LocalDateTime fin;
	
	@Column
	private double distancia;
	
	@Column
	private double precio;
	
	@Column
	private int userId;
	
	@Column 
	private int idMonopatin;
	
    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    private List<Pausa> pauses;

	public Viaje(LocalDateTime inicio, int userId, int idMonopatin) {
		this.inicio = inicio;
		this.userId = userId;
		this.idMonopatin = idMonopatin;
		this.pauses = new ArrayList<>();
	}
	
	public Viaje() {}

	public int getId() {return id;}
	public LocalDateTime getStartTime() {return inicio;}
	public LocalDateTime getEndTime() {return fin;}
	public double getDistance() {return distancia;}
	public double getPrice() {return precio;}
	public int getUserId() {return userId;}
	public int getIdMonopatin() {return idMonopatin;}
    public List<Pausa> getPauses() {return pauses;}
	
	public void setEndTime(LocalDateTime fin) {this.fin = fin;}
	public void setDistance(double distancia) {this.distancia = distancia;}
	public void setPrice(double precio) {this.precio = precio;}
}
