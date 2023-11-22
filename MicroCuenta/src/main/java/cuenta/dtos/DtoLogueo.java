package cuenta.dtos;

public class DtoLogueo {
    private String email;
    private String password;

    public DtoLogueo(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public DtoLogueo(){}

    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
}