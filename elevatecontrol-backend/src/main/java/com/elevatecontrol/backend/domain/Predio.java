package com.elevatecontrol.backend.domain;

import jakarta.persistence.*;

@Entity
@Table(name="predios")
public class Predio {
    @Id @Column(length=64, nullable=false)
    private String id;
    @Column(nullable=false, length=64)
    private String clienteId;
    @Column(nullable=false, length=160)
    private String nome;
    @Column(nullable=false, length=300)
    private String endereco;

    public Predio() {}
    public String getId() { return id; }
    public void setId(String value) { this.id=value; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String value) { this.clienteId=value; }
    public String getNome() { return nome; }
    public void setNome(String value) { this.nome=value; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String value) { this.endereco=value; }
}