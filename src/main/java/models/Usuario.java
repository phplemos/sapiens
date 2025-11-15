package models;

import enums.TipoPerfilUsuario;

import java.time.LocalDateTime;

public class Usuario {

    private int pessoaId;
    private String emailLogin;
    private String senhaHash;
    private TipoPerfilUsuario tipoPerfil;
    private LocalDateTime criadoEm;

    public int getPessoaId() {
        return pessoaId;
    }

    public void setPessoaId(int pessoaId) {
        this.pessoaId = pessoaId;
    }

    public String getEmailLogin() {
        return emailLogin;
    }

    public void setEmailLogin(String emailLogin) {
        this.emailLogin = emailLogin;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public TipoPerfilUsuario getTipoPerfil() {
        return tipoPerfil;
    }

    public void setTipoPerfil(TipoPerfilUsuario tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
