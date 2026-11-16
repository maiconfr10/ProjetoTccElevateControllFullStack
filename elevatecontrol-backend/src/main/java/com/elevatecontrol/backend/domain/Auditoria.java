package com.elevatecontrol.backend.domain;

import jakarta.persistence.*;

@Entity
@Table(name="auditoria")
public class Auditoria {
    @Id @Column(length=64, nullable=false)
    private String id;
    @Column(length=64)
    private String usuarioId;
    @Column(nullable=false, length=80)
    private String acao;
    @Column(nullable=false, length=80)
    private String recurso;
    @Column(length=64)
    private String recursoId;
    @Column(nullable=false)
    private java.time.LocalDateTime quando;

    public Auditoria() {}
    public String getId() { return id; }
    public void setId(String value) { this.id=value; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String value) { this.usuarioId=value; }
    public String getAcao() { return acao; }
    public void setAcao(String value) { this.acao=value; }
    public String getRecurso() { return recurso; }
    public void setRecurso(String value) { this.recurso=value; }
    public String getRecursoId() { return recursoId; }
    public void setRecursoId(String value) { this.recursoId=value; }
    public java.time.LocalDateTime getQuando() { return quando; }
    public void setQuando(java.time.LocalDateTime value) { this.quando=value; }
}