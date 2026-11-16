package com.elevatecontrol.backend.service;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.domain.*;
import com.elevatecontrol.backend.repository.*;
import com.elevatecontrol.backend.security.ScopeService;
import com.elevatecontrol.backend.support.*;
import java.security.SecureRandom;
import java.util.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class UsuarioService {
 private final UsuarioRepository repo;private final ClienteRepository clients;
 private final ScopeService scope;private final AuditService audit;private final PasswordEncoder encoder;
 public UsuarioService(UsuarioRepository r,ClienteRepository c,ScopeService s,AuditService a,PasswordEncoder p){repo=r;clients=c;scope=s;audit=a;encoder=p;}
 @Transactional(readOnly=true)public List<UsuarioDto> list(){scope.requireAdmin();return repo.findAll().stream().map(Mapper::user).toList();}
 @Transactional(readOnly=true)public UsuarioDto get(String id){scope.requireAdmin();return Mapper.user(repo.findById(id).orElseThrow(()->ApiException.notFound("Usuario")));}
 @Transactional public UsuarioCriado create(CriarUsuario req){
   scope.requireAdmin();
   if(!Set.of("admin","tecnico","cliente").contains(req.perfil()))throw ApiException.bad("Perfil invalido.");
   String email=req.email().trim().toLowerCase(Locale.ROOT);
   if(repo.existsByEmailIgnoreCase(email))throw ApiException.conflict("E-mail ja cadastrado.");
   if("cliente".equals(req.perfil())){
      if(req.clienteId()==null)throw ApiException.bad("ClienteId obrigatorio para perfil cliente.");
      clients.findById(req.clienteId()).orElseThrow(()->ApiException.notFound("Cliente"));
   }else if(req.clienteId()!=null&&!req.clienteId().isBlank())throw ApiException.bad("Somente perfil cliente pode informar clienteId.");
   String temporary=req.senhaInicial()==null||req.senhaInicial().isBlank()?password():req.senhaInicial();
   if(temporary.length()<12)throw ApiException.bad("Senha inicial requer ao menos 12 caracteres.");
   Usuario u=new Usuario();u.setId(Ids.next("USR"));u.setNome(req.nome().trim());u.setEmail(email);u.setPerfil(req.perfil());u.setRegistro(req.registro());
   u.setClienteId("cliente".equals(req.perfil())?req.clienteId():null);u.setSenhaHash(encoder.encode(temporary));u.setAtivo(true);u.setTrocaSenhaObrigatoria(true);
   repo.save(u);audit.record("CRIAR","Usuario",u.getId());
   return new UsuarioCriado(u.getId(),u.getNome(),u.getEmail(),u.getPerfil(),u.getRegistro(),u.getClienteId(),temporary);
 }
 private static String password(){byte[] raw=new byte[18];new SecureRandom().nextBytes(raw);return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);}
}
