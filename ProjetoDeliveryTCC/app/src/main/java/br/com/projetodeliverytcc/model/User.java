package br.com.projetodeliverytcc.model;

/**
 * Created by daniel on 12/02/18.
 */

public class User {

    private String name;
    private String password;
    private String Phone;
    private String isStaff;
    private String secureCode;

    public User(){

    }

    public User(String name, String password, String secureCode) {
        this.name = name;
        this.password = password;
        this.isStaff = "false";
        this.secureCode = secureCode;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
