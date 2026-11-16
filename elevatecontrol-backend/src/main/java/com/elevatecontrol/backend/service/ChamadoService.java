package com.elevatecontrol.backend.service;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.domain.*;
import com.elevatecontrol.backend.repository.*;
import com.elevatecontrol.backend.security.ScopeService;
import com.elevatecontrol.backend.support.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ChamadoService {
 private final ChamadoRepository repo;private final PredioRepository buildings;private final ElevadorRepository elevators;
 private final UsuarioRepository users;private final ScopeService scope;private final AuditService audit;
 public ChamadoService(ChamadoRepository r,PredioRepository b,ElevadorRepository e,UsuarioRepository u,ScopeService s,AuditService a){repo=r;buildings=b;elevators=e;users=u;scope=s;audit=a;}
 @Transactional(readOnly=true) public List<ChamadoDto> list(String status,String search){
   Usuario user=scope.current();
   List<Chamado> all=scope.admin(user)?repo.findAll():scope.tecnico(user)?repo.findByTecnicoId(user.getId()):repo.findByPredioIdIn(scope.buildingIds(user));
   return all.stream().filter(t->status==null||status.isBlank()||t.getStatus().equals(status))
     .filter(t->search==null||search.isBlank()||(t.getId()+" "+t.getTitulo()+" "+t.getDescricao()).toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT)))
     .sorted(Comparator.comparing(Chamado::getData).reversed()).map(t->visible(t,user)).toList();
 }
 @Transactional(readOnly=true)public ChamadoDto get(String id){var t=existing(id);scope.canSeeTicket(t);return visible(t,scope.current());}
 public Chamado existing(String id){return repo.findById(id).orElseThrow(()->ApiException.notFound("Chamado"));}
 @Transactional public ChamadoDto create(CriarChamado req){
   var u=scope.current();if(scope.tecnico(u))throw ApiException.forbidden();
   if(!Set.of("Corretiva","Preventiva","Emergência").contains(req.tipo()))throw ApiException.bad("Tipo invalido.");
   if(!Set.of("Normal","Alta","Crítica").contains(req.prioridade()))throw ApiException.bad("Prioridade invalida.");
   Predio p=buildings.findById(req.predioId()).orElseThrow(()->ApiException.notFound("Predio"));
   Elevador e=elevators.findById(req.elevadorId()).orElseThrow(()->ApiException.notFound("Elevador"));
   if(!e.getPredioId().equals(p.getId()))throw ApiException.bad("Elevador nao pertence ao predio.");
   if(scope.cliente(u)&&!Objects.equals(u.getClienteId(),p.getClienteId()))throw ApiException.forbidden();
   if(scope.cliente(u)&&req.tecnicoId()!=null)throw ApiException.forbidden();
   if(req.tecnicoId()!=null)verifyTechnician(req.tecnicoId());
   Chamado t=new Chamado();t.setId(Ids.next("CH"));t.setTitulo(req.titulo().trim());t.setDescricao(req.descricao().trim());
   t.setPredioId(req.predioId());t.setElevadorId(req.elevadorId());t.setTipo(req.tipo());
   // Emergencias nunca podem ser rebaixadas para prioridade normal pelo front/IA.
   String priority="Emergência".equals(req.tipo())||emergency(req.descricao())?"Crítica":req.prioridade();
   t.setPrioridade(priority);t.setStatus("Aberto");t.setTecnicoId(req.tecnicoId());t.setCriadoPor(u.getId());t.setData(LocalDateTime.now());
   repo.save(t);audit.record("ABRIR","Chamado",t.getId());return visible(t,u);
 }
 private ChamadoDto visible(Chamado t,Usuario requester){
   var dto=Mapper.ticket(t);
   if(!scope.cliente(requester))return dto;
   // O cliente vê o andamento do serviço, não a distribuição interna de técnicos.
   return new ChamadoDto(dto.id(),dto.titulo(),dto.descricao(),dto.predioId(),dto.elevadorId(),
     dto.tipo(),dto.prioridade(),dto.status(),null,dto.criadoPor(),dto.data(),dto.concluidoEm());
 }
 static boolean emergency(String desc){
   String s=java.text.Normalizer.normalize(desc,java.text.Normalizer.Form.NFD).replaceAll("\\p{M}","").toLowerCase(Locale.ROOT);
   return s.contains("pessoas presas")||s.contains("pessoa presa")||s.contains("passageiros presos")||s.contains("passageiro preso")||s.contains("aprisionamento");
 }
 private void verifyTechnician(String id){
   Usuario tech=users.findById(id).orElseThrow(()->ApiException.notFound("Tecnico"));
   if(!"tecnico".equals(tech.getPerfil())||!tech.getAtivo())throw ApiException.bad("Responsavel nao e tecnico ativo.");
 }
 @Transactional public ChamadoDto assign(String id,String tech){
   scope.requireAdmin();Chamado t=existing(id);
   if("Concluído".equals(t.getStatus()))throw ApiException.bad("Nao pode atribuir chamado concluido.");
   verifyTechnician(tech);t.setTecnicoId(tech);repo.save(t);audit.record("ATRIBUIR","Chamado",id);return Mapper.ticket(t);
 }
 @Transactional public ChamadoDto start(String id){
   Chamado t=existing(id);scope.canWork(t);
   if(t.getTecnicoId()==null)throw ApiException.bad("Atribua um tecnico antes de iniciar.");
   if(!"Aberto".equals(t.getStatus()))throw ApiException.bad("Somente chamados abertos podem ser iniciados.");
   t.setStatus("Em andamento");repo.save(t);audit.record("INICIAR","Chamado",id);return Mapper.ticket(t);
 }
 @Transactional public ChamadoDto changeStatus(String id,String status){
   scope.requireAdmin();Chamado t=existing(id);
   if(!Set.of("Aberto","Em andamento").contains(status))throw ApiException.bad("Conclusao ocorre somente por visita regularizada.");
   t.setStatus(status);t.setConcluidoEm(null);repo.save(t);audit.record("STATUS","Chamado",id);return Mapper.ticket(t);
 }
}
