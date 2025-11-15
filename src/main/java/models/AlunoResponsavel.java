package models;

public class AlunoResponsavel {
    private int alunoPessoaId;
    private int responsavelPessoaId;

    public AlunoResponsavel() {}

    public int getAlunoPessoaId() {
        return alunoPessoaId;
    }

    public void setAlunoPessoaId(int alunoPessoaId) {
        this.alunoPessoaId = alunoPessoaId;
    }

    public int getResponsavelPessoaId() {
        return responsavelPessoaId;
    }

    public void setResponsavelPessoaId(int responsavelPessoaId) {
        this.responsavelPessoaId = responsavelPessoaId;
    }
}
