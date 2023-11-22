package config.dtos;

import java.time.LocalDate;

public class DtoPrecios {
	
	private double tarifa;
	private double tarifa2;
	private LocalDate fechaCambio;
	
	public DtoPrecios(double tarifa, double tarifa2, LocalDate fechaCambio) {
		this.tarifa = tarifa;
		this.tarifa2 = tarifa2;
		this.fechaCambio = fechaCambio;
	}

	public double getStandardPrice() {return tarifa;}
	public double getExtendedPausePrice() {return tarifa2;}
	public LocalDate getStartDate() {return fechaCambio;}
}