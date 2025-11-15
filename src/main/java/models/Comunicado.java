package models;

import java.time.LocalDateTime;

public class Comunicado {
    private int id;
    private int remetentePessoaId;
    private String titulo;
    private String corpo;
    private LocalDateTime dataEnvio;

    public Comunicado() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRemetentePessoaId() {
        return remetentePessoaId;
    }

    public void setRemetentePessoaId(int remetentePessoaId) {
        this.remetentePessoaId = remetentePessoaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }
}
