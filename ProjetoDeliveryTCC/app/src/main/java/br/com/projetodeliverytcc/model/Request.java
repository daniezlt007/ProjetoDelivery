package br.com.projetodeliverytcc.model;

import java.util.List;

/**
 * Created by daniel on 02/03/18.
 */

public class Request {

    private String phone;
    private String nome;
    private String address;
    private String total;
    private String status;
    private String comment;
    private String paymentState;
    private List<Order> produtos;

    public Request() {

    }

    public Request(String phone, String nome, String address, String total, String comment, String paymentState, List<Order> produtos, String status) {
        this.phone = phone;
        this.nome = nome;
        this.address = address;
        this.total = total;
        this.status = status;
        this.comment = comment;
        this.paymentState = paymentState;
        this.produtos = produtos;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public List<Order> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Order> produtos) {
        this.produtos = produtos;
    }
}
