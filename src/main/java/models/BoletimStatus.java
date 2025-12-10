package models;

public class BoletimStatus {
    private int id;
    private int turmaId;
    private int periodoLetivoId;
    private boolean publicado;

    public BoletimStatus() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTurmaId() { return turmaId; }
    public void setTurmaId(int turmaId) { this.turmaId = turmaId; }
    public int getPeriodoLetivoId() { return periodoLetivoId; }
    public void setPeriodoLetivoId(int periodoLetivoId) { this.periodoLetivoId = periodoLetivoId; }
    public boolean isPublicado() { return publicado; }
    public void setPublicado(boolean publicado) { this.publicado = publicado; }
}