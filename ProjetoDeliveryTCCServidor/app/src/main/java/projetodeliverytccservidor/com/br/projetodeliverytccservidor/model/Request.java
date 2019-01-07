package projetodeliverytccservidor.com.br.projetodeliverytccservidor.model;

import java.util.List;

/**
 * Created by daniel on 02/03/18.
 */

public class Request {

    private String phone;
    private String nome;
    private String address;
    private String total;
    private List<Order> produtos;
    private String status;
    private String comment;

    public Request() {
    }

    public Request(String phone, String nome, String address, String total, List<Order> produtos, String comment) {
        this.phone = phone;
        this.nome = nome;
        this.address = address;
        this.total = total;
        this.produtos = produtos;
        this.status = "0";//0=PEDIDO RECEBIDO, 1=CONFERINDO PEDIDO, 2=PEDIDO ENVIADO
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Order> produtos) {
        this.produtos = produtos;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
