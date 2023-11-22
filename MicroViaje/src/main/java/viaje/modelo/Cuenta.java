package viaje.modelo;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Cuenta {
    private int id;
    @JsonIgnore
    private LocalDate fechaAlta;
    private double saldo;
    private String mercadoPagoId;
    private boolean anulada;
    public Cuenta() {
    }
    public Cuenta(int id, LocalDate fechaAlta, double saldo, String mercadoPagoId, boolean anulada) {
        this.id = id;
        this.fechaAlta = fechaAlta;
        this.saldo = saldo;
        this.mercadoPagoId = mercadoPagoId;
        this.anulada = anulada;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public LocalDate getFechaAlta() {
        return fechaAlta;
    }
    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
    public double getSaldo() {
        return saldo;
    }
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
    public String getMercadoPagoId() {
        return mercadoPagoId;
    }
    public void setMercadoPagoId(String mercadoPagoId) {
        this.mercadoPagoId = mercadoPagoId;
    }
    public boolean  getAnulada() {
        return anulada;
    }
    public void setAnulada(boolean anulada) {
        this.anulada = anulada;
    }   
}