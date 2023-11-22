package cuenta.dtos;

public class DtoAutenticidad {
    private String token;

    public DtoAutenticidad(){}
    public DtoAutenticidad(String token) {
        this.token = token;
    }
    
    public String getToken() {return token;}
}