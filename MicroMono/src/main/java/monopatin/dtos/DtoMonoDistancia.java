package monopatin.dtos;

import java.time.LocalDate;

public class DtoMonoDistancia {
    private int id;
	private String estado;
	private double latitud;
	private double longitud;
	private LocalDate ultimoMantenimiento;
    private double DistanciaTotal;
    
    public DtoMonoDistancia() {
    }

    public DtoMonoDistancia(int id, String estado, double latitud, double longitud,
            LocalDate ultimoMantenimiento, double DistanciaTotal) {
        this.id = id;
        this.estado = estado;
        this.latitud = latitud;
        this.longitud = longitud;
        this.ultimoMantenimiento = ultimoMantenimiento;
        this.DistanciaTotal = DistanciaTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public LocalDate getUltimoMantenimiento() {
        return ultimoMantenimiento;
    }

    public void setUltimoMantenimiento(LocalDate ultimoMantenimiento) {
        this.ultimoMantenimiento = ultimoMantenimiento;
    }

    public double getDistanciaTotal() {
        return DistanciaTotal;
    }

    public void setDistanciaTotal(double DistanciaTotal) {
        this.DistanciaTotal = DistanciaTotal;
    }
}