package projetodeliverytccservidor.com.br.projetodeliverytccservidor.model;

public class Token {

    private String token;
    private boolean isServerToken;

    public Token() {

    }

    public Token(String token, boolean isServerToken) {
        this.token = token;
        this.isServerToken = isServerToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean getIsServerToken() {
        return isServerToken;
    }

    public void setIsServerToken(boolean isServerToken) {
        this.isServerToken = isServerToken;
    }

}