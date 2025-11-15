package models;

import enums.StatusMatricula;

import java.time.LocalDate;

public class Matricula {
    private int id;
    private int alunoPessoaId;
    private int turmaId;
    private String numeroMatricula;
    private LocalDate dataMatricula;
    private StatusMatricula status;

    public Matricula() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlunoPessoaId() {
        return alunoPessoaId;
    }

    public void setAlunoPessoaId(int alunoPessoaId) {
        this.alunoPessoaId = alunoPessoaId;
    }

    public int getTurmaId() {
        return turmaId;
    }

    public void setTurmaId(int turmaId) {
        this.turmaId = turmaId;
    }

    public String getNumeroMatricula() {
        return numeroMatricula;
    }

    public void setNumeroMatricula(String numeroMatricula) {
        this.numeroMatricula = numeroMatricula;
    }

    public LocalDate getDataMatricula() {
        return dataMatricula;
    }

    public void setDataMatricula(LocalDate dataMatricula) {
        this.dataMatricula = dataMatricula;
    }

    public StatusMatricula getStatus() {
        return status;
    }

    public void setStatus(StatusMatricula status) {
        this.status = status;
    }
}
