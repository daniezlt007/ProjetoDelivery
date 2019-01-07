package projetodeliverytccservidor.com.br.projetodeliverytccservidor.model;

/**
 * Created by daniel on 04/03/18.
 */

public class User {

    private String name;
    private String phone;
    private String password;
    private String isStaff;

    public User() {

    }

    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }
}
