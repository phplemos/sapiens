package models;

public class ParametrosEscola {
    private int id; // Geralmente será sempre 1
    private double mediaAprovacao;
    private double mediaRecuperacao;
    private int limiteFaltas;
    private String nomeInstituicao;

    public ParametrosEscola() {
        // Valores Padrão (caso seja criado pela primeira vez)
        this.mediaAprovacao = 7.0;
        this.mediaRecuperacao = 4.0; // Abaixo disso reprova direto
        this.limiteFaltas = 25; // % ou quantidade absoluta
        this.nomeInstituicao = "Escola Sapiens";
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getMediaAprovacao() { return mediaAprovacao; }
    public void setMediaAprovacao(double mediaAprovacao) { this.mediaAprovacao = mediaAprovacao; }
    public double getMediaRecuperacao() { return mediaRecuperacao; }
    public void setMediaRecuperacao(double mediaRecuperacao) { this.mediaRecuperacao = mediaRecuperacao; }
    public int getLimiteFaltas() { return limiteFaltas; }
    public void setLimiteFaltas(int limiteFaltas) { this.limiteFaltas = limiteFaltas; }
    public String getNomeInstituicao() { return nomeInstituicao; }
    public void setNomeInstituicao(String nomeInstituicao) { this.nomeInstituicao = nomeInstituicao; }
}