package models;

public class MatriculaDisciplina {
    private int id;
    private int matriculaId;
    private int turmaDisciplinaId;
    private int totalFaltas;

    public MatriculaDisciplina() {}

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

    public int getTurmaDisciplinaId() {
        return turmaDisciplinaId;
    }

    public void setTurmaDisciplinaId(int turmaDisciplinaId) {
        this.turmaDisciplinaId = turmaDisciplinaId;
    }

    public int getTotalFaltas() {
        return totalFaltas;
    }

    public void setTotalFaltas(int totalFaltas) {
        this.totalFaltas = totalFaltas;
    }
}
