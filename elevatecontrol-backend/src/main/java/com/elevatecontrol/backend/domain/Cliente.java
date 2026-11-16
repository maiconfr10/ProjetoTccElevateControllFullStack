package com.elevatecontrol.backend.domain;

import jakarta.persistence.*;

@Entity
@Table(name="clientes")
public class Cliente {
    @Id @Column(length=64, nullable=false)
    private String id;
    @Column(nullable=false, length=160)
    private String nome;
    @Column(nullable=false, length=40)
    private String documento;
    @Column(nullable=false, length=140)
    private String contato;
    @Column(nullable=false, length=180)
    private String email;
    @Column(nullable=false, length=40)
    private String telefone;

    public Cliente() {}
    public String getId() { return id; }
    public void setId(String value) { this.id=value; }
    public String getNome() { return nome; }
    public void setNome(String value) { this.nome=value; }
    public String getDocumento() { return documento; }
    public void setDocumento(String value) { this.documento=value; }
    public String getContato() { return contato; }
    public void setContato(String value) { this.contato=value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { this.email=value; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String value) { this.telefone=value; }
}