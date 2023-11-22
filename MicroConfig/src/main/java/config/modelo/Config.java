package config.modelo;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("fares")
public class Config {
	
	@Id
	private String id;
	
	private double tarifa;
	private double tarifa2;
	private LocalDate fechaCambio;

	public Config(double tarifa, double tarifa2, LocalDate fechaCambio) {
		this.tarifa = tarifa;
		this.tarifa2 = tarifa2;
		this.fechaCambio = fechaCambio;
	}
	
	public Config() {}

	public String getId() {return id;}
	public double getStandardPrice() {return tarifa;}
	public double getExtendedPausePrice() {return tarifa2;}
	public LocalDate getStartDate() {return fechaCambio;}
}