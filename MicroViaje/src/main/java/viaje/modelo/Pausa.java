package viaje.modelo;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Pausa {
	
	@Id @GeneratedValue (strategy = GenerationType.AUTO)
	private int id;
	
	@Column
	private LocalTime inicio;
	
	@Column
	private LocalTime fin;
	
	@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "ride_id")
    private Viaje ride;
	
	public Pausa(LocalTime inicio, Viaje ride) {
		this.inicio = inicio;
        this.ride = ride;
	}
	
	public Pausa() {}

	public int getId() {return id;}
	public LocalTime getInicio() {return inicio;}
	public LocalTime getFin() {return fin;}
    public Viaje getRide() {return ride;}
    
    public void setRide(Viaje ride) {this.ride = ride;}
    public void setEndTime(LocalTime fin) {this.fin = fin;}
}