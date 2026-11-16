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
public class PredioService {
 private final PredioRepository repo;private final ClienteRepository clients;private final ScopeService scope;private final AuditService audit;
 public PredioService(PredioRepository r,ClienteRepository c,ScopeService s,AuditService a){repo=r;clients=c;scope=s;audit=a;}
 @Transactional(readOnly=true)public List<PredioDto> list(){
   Set<String> ids=scope.buildingIds(scope.current());
   return repo.findAll().stream().filter(p->ids.contains(p.getId())).map(Mapper::building).toList();
 }
 @Transactional(readOnly=true)public PredioDto get(String id){scope.canSeeBuilding(id);return Mapper.building(repo.findById(id).orElseThrow(()->ApiException.notFound("Predio")));}
 @Transactional public PredioDto create(CriarPredio req){
   scope.requireAdmin();clients.findById(req.clienteId()).orElseThrow(()->ApiException.notFound("Cliente"));
   Predio p=new Predio();p.setId(Ids.next("PRD"));p.setClienteId(req.clienteId());p.setNome(req.nome().trim());p.setEndereco(req.endereco().trim());
   repo.save(p);audit.record("CRIAR","Predio",p.getId());return Mapper.building(p);
 }
}
