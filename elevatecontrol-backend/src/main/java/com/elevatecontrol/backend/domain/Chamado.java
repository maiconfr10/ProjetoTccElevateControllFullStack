package com.elevatecontrol.backend.domain;

import jakarta.persistence.*;

@Entity
@Table(name="chamados")
public class Chamado {
    @Id @Column(length=64, nullable=false)
    private String id;
    @Column(nullable=false, length=100)
    private String titulo;
    @Column(columnDefinition="LONGTEXT")
    private String descricao;
    @Column(nullable=false, length=64)
    private String predioId;
    @Column(nullable=false, length=64)
    private String elevadorId;
    @Column(nullable=false, length=24)
    private String tipo;
    @Column(nullable=false, length=24)
    private String prioridade;
    @Column(nullable=false, length=40)
    private String status;
    @Column(length=64)
    private String tecnicoId;
    @Column(nullable=false, length=64)
    private String criadoPor;
    @Column(nullable=false)
    private java.time.LocalDateTime data;
    private java.time.LocalDateTime concluidoEm;
    @Version
    private long versao;

    public Chamado() {}
    public String getId() { return id; }
    public void setId(String value) { this.id=value; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String value) { this.titulo=value; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String value) { this.descricao=value; }
    public String getPredioId() { return predioId; }
    public void setPredioId(String value) { this.predioId=value; }
    public String getElevadorId() { return elevadorId; }
    public void setElevadorId(String value) { this.elevadorId=value; }
    public String getTipo() { return tipo; }
    public void setTipo(String value) { this.tipo=value; }
    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String value) { this.prioridade=value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status=value; }
    public String getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(String value) { this.tecnicoId=value; }
    public String getCriadoPor() { return criadoPor; }
    public void setCriadoPor(String value) { this.criadoPor=value; }
    public java.time.LocalDateTime getData() { return data; }
    public void setData(java.time.LocalDateTime value) { this.data=value; }
    public java.time.LocalDateTime getConcluidoEm() { return concluidoEm; }
    public void setConcluidoEm(java.time.LocalDateTime value) { this.concluidoEm=value; }
    public long getVersao() { return versao; }
    public void setVersao(long value) { this.versao=value; }
}