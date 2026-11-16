package com.elevatecontrol.backend.domain;

import jakarta.persistence.*;

@Entity
@Table(name="usuarios")
public class Usuario {
    @Id @Column(length=64, nullable=false)
    private String id;
    @Column(nullable=false, length=120)
    private String nome;
    @Column(nullable=false, length=180, unique=true)
    private String email;
    @Column(name="senha_hash", nullable=false, length=100)
    private String senhaHash;
    @Column(nullable=false, length=20)
    private String perfil;
    @Column(length=120)
    private String registro;
    @Column(length=64)
    private String clienteId;
    @Column(nullable=false)
    private boolean ativo;
    @Column(name="troca_senha_obrigatoria", nullable=false)
    private boolean trocaSenhaObrigatoria;
    @Column(name="configuracao_inicial_pendente", nullable=false)
    private boolean configuracaoInicialPendente;

    public Usuario() {}
    public boolean getTrocaSenhaObrigatoria() {return trocaSenhaObrigatoria;}
    public void setTrocaSenhaObrigatoria(boolean value) {trocaSenhaObrigatoria=value;}
    public boolean getConfiguracaoInicialPendente() {return configuracaoInicialPendente;}
    public void setConfiguracaoInicialPendente(boolean value) {configuracaoInicialPendente=value;}
    public String getId() { return id; }
    public void setId(String value) { this.id=value; }
    public String getNome() { return nome; }
    public void setNome(String value) { this.nome=value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { this.email=value; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String value) { this.senhaHash=value; }
    public String getPerfil() { return perfil; }
    public void setPerfil(String value) { this.perfil=value; }
    public String getRegistro() { return registro; }
    public void setRegistro(String value) { this.registro=value; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String value) { this.clienteId=value; }
    public boolean getAtivo() { return ativo; }
    public void setAtivo(boolean value) { this.ativo=value; }
}