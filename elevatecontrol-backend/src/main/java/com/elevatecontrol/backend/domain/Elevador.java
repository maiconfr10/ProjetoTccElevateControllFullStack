package com.elevatecontrol.backend.domain;

import jakarta.persistence.*;

@Entity
@Table(name="elevadores")
public class Elevador {
    @Id @Column(length=64, nullable=false)
    private String id;
    @Column(nullable=false, length=64)
    private String predioId;
    @Column(nullable=false, length=120)
    private String identificacao;
    @Column(nullable=false, length=120)
    private String marca;
    @Column(nullable=false, length=120)
    private String modelo;
    @Column(nullable=false, length=40)
    private String status;
    @Column(length=2000)
    private String pendencia;

    public Elevador() {}
    public String getId() { return id; }
    public void setId(String value) { this.id=value; }
    public String getPredioId() { return predioId; }
    public void setPredioId(String value) { this.predioId=value; }
    public String getIdentificacao() { return identificacao; }
    public void setIdentificacao(String value) { this.identificacao=value; }
    public String getMarca() { return marca; }
    public void setMarca(String value) { this.marca=value; }
    public String getModelo() { return modelo; }
    public void setModelo(String value) { this.modelo=value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status=value; }
    public String getPendencia() { return pendencia; }
    public void setPendencia(String value) { this.pendencia=value; }
}