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
public class ElevadorService {
 private final ElevadorRepository repo;private final PredioRepository buildings;private final ScopeService scope;private final AuditService audit;
 private static final Set<String> ESTADOS=Set.of("Operacional","Em manutenção","Fora de serviço");
 public ElevadorService(ElevadorRepository r,PredioRepository b,ScopeService s,AuditService a){repo=r;buildings=b;scope=s;audit=a;}
 @Transactional(readOnly=true)public List<ElevadorDto> list(){
   var ids=scope.buildingIds(scope.current());
   return repo.findAll().stream().filter(e->ids.contains(e.getPredioId())).map(Mapper::elevator).toList();
 }
 @Transactional(readOnly=true)public ElevadorDto get(String id){
   var e=repo.findById(id).orElseThrow(()->ApiException.notFound("Elevador"));
   scope.canSeeBuilding(e.getPredioId());return Mapper.elevator(e);
 }
 @Transactional(readOnly=true)public List<ElevadorDto> byBuilding(String id){scope.canSeeBuilding(id);return repo.findByPredioId(id).stream().map(Mapper::elevator).toList();}
 @Transactional public ElevadorDto create(CriarElevador req){
   scope.requireAdmin();buildings.findById(req.predioId()).orElseThrow(()->ApiException.notFound("Predio"));
   if(!ESTADOS.contains(req.status()))throw ApiException.bad("Status de elevador invalido.");
   Elevador e=new Elevador();e.setId(Ids.next("ELV"));e.setPredioId(req.predioId());e.setIdentificacao(req.identificacao());
   e.setMarca(req.marca());e.setModelo(req.modelo());e.setStatus(req.status());e.setPendencia(req.pendencia());
   repo.save(e);audit.record("CRIAR","Elevador",e.getId());return Mapper.elevator(e);
 }
 @Transactional public ElevadorDto update(String id,AtualizarElevador req){
   scope.requireAdmin();Elevador e=repo.findById(id).orElseThrow(()->ApiException.notFound("Elevador"));
   if(req.predioId()!=null&&!req.predioId().equals(e.getPredioId()))throw ApiException.bad("Nao e permitido transferir o elevador entre predios.");
   if(req.status()!=null){if(!ESTADOS.contains(req.status()))throw ApiException.bad("Status invalido.");e.setStatus(req.status());}
   if(req.identificacao()!=null)e.setIdentificacao(req.identificacao());
   if(req.marca()!=null)e.setMarca(req.marca());if(req.modelo()!=null)e.setModelo(req.modelo());
   if(req.pendencia()!=null)e.setPendencia(req.pendencia());
   repo.save(e);audit.record("EDITAR","Elevador",e.getId());return Mapper.elevator(e);
 }
}
