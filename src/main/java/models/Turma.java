package models;

import enums.TurnoTurma;

public class Turma {
    private int id;
    private String nome;
    private TurnoTurma turno;
    private int serieId;
    private int anoEscolarId;

    public Turma() {}

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

    public TurnoTurma getTurno() {
        return turno;
    }

    public void setTurno(TurnoTurma turno) {
        this.turno = turno;
    }

    public int getSerieId() {
        return serieId;
    }

    public void setSerieId(int serieId) {
        this.serieId = serieId;
    }

    public int getAnoEscolarId() {
        return anoEscolarId;
    }

    public void setAnoEscolarId(int anoEscolarId) {
        this.anoEscolarId = anoEscolarId;
    }
}
