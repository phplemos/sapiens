package models;

import enums.TipoTransacao;

public class CategoriaFinanceira {
    private int id;
    private String nome;
    private TipoTransacao tipo;

    public CategoriaFinanceira() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }
}
