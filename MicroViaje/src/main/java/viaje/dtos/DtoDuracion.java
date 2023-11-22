package viaje.dtos;

public class DtoDuracion {
	
	private Integer idMonopatin;
	private Long totalTimeSeconds;
	
	public DtoDuracion(Integer idMonopatin, Long totalTime) {
		super();
		this.idMonopatin = idMonopatin;
        this.totalTimeSeconds = totalTime;
	}
	public DtoDuracion() {}

	public Integer getIdMonopatin() {return idMonopatin;}
	public void setIdMonopatin(Integer idMonopatin) {this.idMonopatin = idMonopatin;}
	public Long getTotalTimeSeconds() {return totalTimeSeconds;}
	public void setTotalTimeSeconds(Long totalTime) {this.totalTimeSeconds = totalTime;}
}