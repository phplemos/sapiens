package models;

import enums.StatusPagamento;

import java.time.LocalDate;

public class Mensalidade {
    private int id;
    private int matriculaId;
    private double valorTotal;
    private double valorDesconto;
    private double valorJurosMulta;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private StatusPagamento statusPagamento;


    public Mensalidade() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(int matriculaId) {
        this.matriculaId = matriculaId;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public double getValorJurosMulta() {
        return valorJurosMulta;
    }

    public void setValorJurosMulta(double valorJurosMulta) {
        this.valorJurosMulta = valorJurosMulta;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }
    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
    
    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }
}
