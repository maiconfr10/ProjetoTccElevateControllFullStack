package com.elevatecontrol.backend.service;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.domain.*;
import com.elevatecontrol.backend.repository.*;
import com.elevatecontrol.backend.security.ScopeService;
import com.elevatecontrol.backend.support.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ClienteService {
 private final ClienteRepository repo;private final ScopeService scope;private final AuditService audit;
 public ClienteService(ClienteRepository r,ScopeService s,AuditService a){repo=r;scope=s;audit=a;}
 @Transactional(readOnly=true)public List<ClienteDto> list(){scope.requireAdmin();return repo.findAll().stream().map(Mapper::client).toList();}
 @Transactional(readOnly=true)public List<ClienteNome> nomesVisiveis(){
   var u=scope.current();
   if(scope.admin(u))return repo.findAll().stream().map(c->new ClienteNome(c.getId(),c.getNome())).toList();
   var ids=scope.buildingIds(u);
   // Apenas clientes relacionados a prédios que este usuário já está autorizado a consultar.
   return scope.prediosVisiveis(u).stream().filter(p->ids.contains(p.getId()))
     .map(p->p.getClienteId()).distinct().map(repo::findById).filter(Optional::isPresent)
     .map(Optional::get).map(c->new ClienteNome(c.getId(),c.getNome())).toList();
 }
 @Transactional(readOnly=true)public ClienteDto get(String id){scope.requireAdmin();return Mapper.client(repo.findById(id).orElseThrow(()->ApiException.notFound("Cliente")));}
 @Transactional public ClienteDto create(CriarCliente req){
   scope.requireAdmin();
   if(repo.existsByDocumento(req.documento().trim()))throw ApiException.conflict("Documento ja cadastrado.");
   Cliente c=new Cliente();c.setId(Ids.next("CLI"));c.setNome(req.nome().trim());c.setDocumento(req.documento().trim());
   c.setContato(req.contato().trim());c.setEmail(req.email().trim());c.setTelefone(req.telefone().trim());
   repo.save(c);audit.record("CRIAR","Cliente",c.getId());return Mapper.client(c);
 }
}
