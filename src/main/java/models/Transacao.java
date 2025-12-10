package models;

import enums.TipoTransacao;

import java.time.LocalDate;

public class Transacao {
    private int id;
    private String descricao;
    private double valor;
    private TipoTransacao tipo;
    private LocalDate dataTransacao;
    private LocalDate dataPagamento;
    private int categoriaId;

    private Integer mensalidadeId;

    public Transacao() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }

    public LocalDate getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDate dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Integer getMensalidadeId() {
        return mensalidadeId;
    }

    public void setMensalidadeId(Integer mensalidadeId) {
        this.mensalidadeId = mensalidadeId;
    }
}
