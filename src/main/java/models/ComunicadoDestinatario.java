package models;

public class ComunicadoDestinatario {
    private int id;
    private int comunicadoId;
    private int destinatarioPessoaId;
    private boolean lido;

    public ComunicadoDestinatario() {}


    public int  getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getComunicadoId() {
        return comunicadoId;
    }

    public void setComunicadoId(int comunicadoId) {
        this.comunicadoId = comunicadoId;
    }

    public int getDestinatarioPessoaId() {
        return destinatarioPessoaId;
    }

    public void setDestinatarioPessoaId(int destinatarioPessoaId) {
        this.destinatarioPessoaId = destinatarioPessoaId;
    }

    public boolean isLido() {
        return lido;
    }

    public void setLido(boolean lido) {
        this.lido = lido;
    }
}
