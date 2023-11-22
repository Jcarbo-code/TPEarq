package cuenta.dtos;

import java.time.LocalDate;

public class DtoCuenta {

	private LocalDate fechaAlta;
	private double saldo;
	private String mercadoPagoId;
	
	public DtoCuenta(LocalDate fechaAlta, double saldo, String mercadoPagoId) {
		super();
		this.fechaAlta = fechaAlta;
		this.saldo = saldo;
		this.mercadoPagoId = mercadoPagoId;
	}

	public DtoCuenta() {}

	public LocalDate getFechaAlta() {return fechaAlta;}
	public double getSaldo() {return saldo;}
	public String getMercadoPagoId() {return mercadoPagoId;}
}