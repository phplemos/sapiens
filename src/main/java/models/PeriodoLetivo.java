package models;

import java.time.LocalDate;

public class PeriodoLetivo {
    private int id;
    private int anoEscolarId;
    private String nome;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    public PeriodoLetivo() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAnoEscolarId() {
        return anoEscolarId;
    }

    public void setAnoEscolarId(int anoEscolarId) {
        this.anoEscolarId = anoEscolarId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }
}
