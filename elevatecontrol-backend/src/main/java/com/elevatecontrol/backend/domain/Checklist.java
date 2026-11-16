package com.elevatecontrol.backend.domain;

import jakarta.persistence.*;

@Entity
@Table(name="checklists")
public class Checklist {
    @Id @Column(length=64, nullable=false)
    private String id;
    @Column(nullable=false, length=64)
    private String chamadoId;
    @Column(length=64)
    private String tecnicoId;
    @Column(nullable=false, length=24)
    private String tipo;
    @Column(nullable=false)
    private java.time.LocalDateTime inicio;
    @Column(nullable=false)
    private java.time.LocalDateTime fim;
    @Column(nullable=false)
    private boolean lubrificacao;
    @Column(nullable=false)
    private boolean testeSeguranca;
    @Column(length=1000)
    private String pecas;
    @Column(columnDefinition="LONGTEXT")
    private String observacoes;
    @Column(nullable=false, length=120)
    private String responsavel;
    @Column(columnDefinition="LONGTEXT")
    private String assinatura;
    @Column(nullable=false)
    private java.time.LocalDateTime concluidoEm;
    @Column(columnDefinition="LONGTEXT")
    private String registroAtendimentoJson;

    public Checklist() {}
    public String getId() { return id; }
    public void setId(String value) { this.id=value; }
    public String getChamadoId() { return chamadoId; }
    public void setChamadoId(String value) { this.chamadoId=value; }
    public String getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(String value) { this.tecnicoId=value; }
    public String getTipo() { return tipo; }
    public void setTipo(String value) { this.tipo=value; }
    public java.time.LocalDateTime getInicio() { return inicio; }
    public void setInicio(java.time.LocalDateTime value) { this.inicio=value; }
    public java.time.LocalDateTime getFim() { return fim; }
    public void setFim(java.time.LocalDateTime value) { this.fim=value; }
    public boolean getLubrificacao() { return lubrificacao; }
    public void setLubrificacao(boolean value) { this.lubrificacao=value; }
    public boolean getTesteSeguranca() { return testeSeguranca; }
    public void setTesteSeguranca(boolean value) { this.testeSeguranca=value; }
    public String getPecas() { return pecas; }
    public void setPecas(String value) { this.pecas=value; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String value) { this.observacoes=value; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String value) { this.responsavel=value; }
    public String getAssinatura() { return assinatura; }
    public void setAssinatura(String value) { this.assinatura=value; }
    public java.time.LocalDateTime getConcluidoEm() { return concluidoEm; }
    public void setConcluidoEm(java.time.LocalDateTime value) { this.concluidoEm=value; }
    public String getRegistroAtendimentoJson() { return registroAtendimentoJson; }
    public void setRegistroAtendimentoJson(String value) { this.registroAtendimentoJson=value; }
}