package models;

public class TurmaDisciplina {
    private int id;
    private int turmaId;
    private int disciplinaId;
    private int professorPessoaId;

    public TurmaDisciplina() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTurmaId() {
        return turmaId;
    }

    public void setTurmaId(int turmaId) {
        this.turmaId = turmaId;
    }

    public int getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(int disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public int getProfessorPessoaId() {
        return professorPessoaId;
    }

    public void setProfessorPessoaId(int professorPessoaId) {
        this.professorPessoaId = professorPessoaId;
    }
}
