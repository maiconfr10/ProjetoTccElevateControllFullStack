package com.elevatecontrol.backend.domain;

import jakarta.persistence.*;

@Entity
@Table(name="tokens_recuperacao")
public class TokenRecuperacao {
    @Id @Column(length=64, nullable=false)
    private String id;
    @Column(nullable=false, length=64)
    private String usuarioId;
    @Column(nullable=false, length=64)
    private String tokenHash;
    @Column(nullable=false)
    private java.time.LocalDateTime expiraEm;
    private java.time.LocalDateTime usadoEm;

    public TokenRecuperacao() {}
    public String getId() { return id; }
    public void setId(String value) { this.id=value; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String value) { this.usuarioId=value; }
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String value) { this.tokenHash=value; }
    public java.time.LocalDateTime getExpiraEm() { return expiraEm; }
    public void setExpiraEm(java.time.LocalDateTime value) { this.expiraEm=value; }
    public java.time.LocalDateTime getUsadoEm() { return usadoEm; }
    public void setUsadoEm(java.time.LocalDateTime value) { this.usadoEm=value; }
}