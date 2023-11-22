package monopatin.dtos;

public class DtoDuracion {
	private Integer idMonopatin;
	private Long totalTimeSeconds;
    public DtoDuracion() {
    }
    public DtoDuracion(Integer idMonopatin, Long totalTimeSeconds) {
        this.idMonopatin = idMonopatin;
        this.totalTimeSeconds = totalTimeSeconds;
    }
    public Integer getIdMonopatin() {
        return idMonopatin;
    }
    public void setIdMonopatin(Integer idMonopatin) {
        this.idMonopatin = idMonopatin;
    }
    public Long getTotalTimeSeconds() {
        return totalTimeSeconds;
    }
    public void setTotalTimeSeconds(Long totalTimeSeconds) {
        this.totalTimeSeconds = totalTimeSeconds;
    }
}