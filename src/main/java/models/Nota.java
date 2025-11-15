package models;

public class Nota {
    private int id;
    private int matriculaDisciplinaId;
    private int periodoLetivoId;
    private double valorNota;

    public Nota() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMatriculaDisciplinaId() {
        return matriculaDisciplinaId;
    }

    public void setMatriculaDisciplinaId(int matriculaDisciplinaId) {
        this.matriculaDisciplinaId = matriculaDisciplinaId;
    }

    public int getPeriodoLetivoId() {
        return periodoLetivoId;
    }

    public void setPeriodoLetivoId(int periodoLetivoId) {
        this.periodoLetivoId = periodoLetivoId;
    }

    public double getValorNota() {
        return valorNota;
    }

    public void setValorNota(double valorNota) {
        this.valorNota = valorNota;
    }
}
