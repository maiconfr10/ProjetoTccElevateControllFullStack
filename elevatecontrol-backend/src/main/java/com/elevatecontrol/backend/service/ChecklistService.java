package com.elevatecontrol.backend.service;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.domain.*;
import com.elevatecontrol.backend.repository.*;
import com.elevatecontrol.backend.security.ScopeService;
import com.elevatecontrol.backend.support.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ChecklistService {
 private final ChecklistRepository repo;private final ChamadoRepository tickets;private final ElevadorRepository elevators;
 private final ScopeService scope;private final AuditService audit;private final ObjectMapper mapper;
 private static final Set<String> VISITAS=Set.of("Mensal","Auditoria","Corretiva","Vistoria");
 private static final Set<String> SITUACOES=Set.of("Atendimento regularizado","Pendência de Material","Solicitação de Apoio Técnico","Orçamento pendente de aprovação","Parado","Outros");
 public ChecklistService(ChecklistRepository r,ChamadoRepository t,ElevadorRepository e,ScopeService s,AuditService a,ObjectMapper o){repo=r;tickets=t;elevators=e;scope=s;audit=a;mapper=o;}
 @Transactional(readOnly=true)public List<ChecklistDto> list(String tipo){
   var u=scope.current();Set<String> ids=scope.tecnico(u)?scope.visitedBuildingIds(u):scope.buildingIds(u);
   List<ChecklistDto> results=new ArrayList<>();
   for(var v:repo.findAll()){
     if(tipo!=null&&!tipo.isBlank()&&!v.getTipo().equals(tipo))continue;
     var t=tickets.findById(v.getChamadoId()).orElse(null);
     if(t!=null && ids.contains(t.getPredioId()))results.add(Mapper.checklist(v,mapper));
   }
   results.sort(Comparator.comparing(ChecklistDto::concluidoEm).reversed());return results;
 }
 @Transactional(readOnly=true)public ChecklistDto get(String id){
   var v=repo.findById(id).orElseThrow(()->ApiException.notFound("Checklist"));
   var t=tickets.findById(v.getChamadoId()).orElseThrow(()->ApiException.notFound("Chamado"));
   scope.canSeeChecklist(v,t);return Mapper.checklist(v,mapper);
 }
 @Transactional(readOnly=true)public List<ChecklistDto> byTicket(String id){
   var t=tickets.findById(id).orElseThrow(()->ApiException.notFound("Chamado"));
   var current=scope.current();
   if(scope.tecnico(current)){
     if(!scope.visitedBuildingIds(current).contains(t.getPredioId()))throw ApiException.forbidden();
   } else scope.canSeeTicket(t);
   return repo.findByChamadoId(id).stream().filter(c->{
     try{scope.canSeeChecklist(c,t);return true;}catch(ApiException ex){return false;}
   }).map(c->Mapper.checklist(c,mapper)).sorted(Comparator.comparing(ChecklistDto::concluidoEm).reversed()).toList();
 }
 @Transactional public ChecklistDto register(CriarChecklist req){
   Chamado t=tickets.findById(req.chamadoId()).orElseThrow(()->ApiException.notFound("Chamado"));
   scope.canWork(t);
   if("Concluído".equals(t.getStatus()))throw ApiException.bad("Chamado concluido nao pode receber visitas.");
   if(t.getTecnicoId()==null)throw ApiException.bad("Atribua um tecnico antes do atendimento.");
   if(req.tecnicoId()!=null&&!req.tecnicoId().equals(t.getTecnicoId()))throw ApiException.bad("Tecnico diferente do responsavel pelo chamado.");
   if(!Set.of("Mensal","Atendimento").contains(req.tipo()))throw ApiException.bad("Tipo de checklist invalido.");
   LocalDateTime start=parse(req.inicio()),end=parse(req.fim());
   if(!end.isAfter(start))throw ApiException.bad("Saida deve ser posterior a chegada.");
   if(req.registroAtendimento()==null)throw ApiException.bad("Registro de atendimento obrigatorio em novas visitas.");
   boolean regularizado=req.registroAtendimento().equipamentos()!=null && !req.registroAtendimento().equipamentos().isEmpty()
     && req.registroAtendimento().equipamentos().stream().allMatch(eq->eq!=null && eq.situacoes()!=null
       && eq.situacoes().size()==1 && "Atendimento regularizado".equals(eq.situacoes().get(0)));
   if(regularizado){
     if(req.responsavel()==null||req.responsavel().isBlank())throw ApiException.bad("Responsavel deve ser informado para concluir.");
     signature(req.assinatura(),true);
   } else if(req.assinatura()!=null&&!req.assinatura().isBlank()){
     signature(req.assinatura(),false);
   }
   RegistroAtendimento registro=validateRecord(req.registroAtendimento(),t,req,regularizado);
   Checklist c=new Checklist();c.setId(Ids.next("CHK"));c.setChamadoId(t.getId());c.setTecnicoId(t.getTecnicoId());c.setTipo(req.tipo());
   c.setInicio(start);c.setFim(end);c.setLubrificacao(Boolean.TRUE.equals(req.lubrificacao()));c.setTesteSeguranca(Boolean.TRUE.equals(req.testeSeguranca()));
   c.setPecas(req.pecas());c.setObservacoes(req.observacoes());c.setResponsavel(req.responsavel()==null||req.responsavel().isBlank()?"Pendente de validação":req.responsavel().trim());
   c.setAssinatura(req.assinatura());c.setConcluidoEm(LocalDateTime.now());
   try{c.setRegistroAtendimentoJson(mapper.writeValueAsString(registro));}catch(JsonProcessingException ex){throw ApiException.bad("Registro invalido.");}
   // Transacao unica: visita + status do chamado + 1-2 elevadores + auditoria.
   repo.save(c);
   boolean allRegularized=regularizado;
   t.setStatus(allRegularized?"Concluído":"Em andamento");
   t.setConcluidoEm(allRegularized?LocalDateTime.now():null);
   tickets.save(t);
   for(RegistroElevador eq:registro.equipamentos()){
     Elevador e=elevators.findById(eq.elevadorId()).orElseThrow(()->ApiException.notFound("Elevador"));
     boolean stopped=eq.situacoes().contains("Parado");
     List<String> pending=eq.situacoes().stream().filter(s->!"Atendimento regularizado".equals(s)).map(s->"Outros".equals(s)?eq.outros():s).toList();
     e.setStatus(stopped?"Fora de serviço":pending.isEmpty()?"Operacional":"Em manutenção");
     e.setPendencia(String.join("; ",pending));elevators.save(e);
   }
   audit.record("REGISTRAR_VISITA","Checklist",c.getId());
   return Mapper.checklist(c,mapper);
 }
 private static LocalDateTime parse(String value){
   try{return LocalDateTime.parse(value);}catch(Exception ex){throw ApiException.bad("Data/hora invalida (use ISO-8601).");}
 }
 private static void signature(String data,boolean required){
   if(data==null||data.isBlank()){
     if(required)throw ApiException.bad("Assinatura digital obrigatoria.");return;
   }
   if(data.length()>450000||!data.startsWith("data:image/png;base64,"))throw ApiException.bad("Assinatura deve ser imagem PNG de ate 330 KB.");
   try{
     byte[] raw=Base64.getDecoder().decode(data.substring("data:image/png;base64,".length()));
     if(raw.length>330000||raw.length<8||raw[0]!=(byte)0x89||raw[1]!=0x50||raw[2]!=0x4e||raw[3]!=0x47)throw ApiException.bad("Assinatura PNG invalida.");
   }catch(IllegalArgumentException ex){throw ApiException.bad("Assinatura PNG invalida.");}
 }
 private RegistroAtendimento validateRecord(RegistroAtendimento input,Chamado t,CriarChecklist req,boolean regularizado){
   if(input.equipamentos()==null||input.equipamentos().isEmpty()||input.equipamentos().size()>2)throw ApiException.bad("A visita exige de 1 a 2 elevadores.");
   if(!t.getElevadorId().equals(input.equipamentos().get(0).elevadorId()))throw ApiException.bad("Primeiro elevador deve ser o do chamado.");
   Set<String> unique=new HashSet<>();boolean monthly=false;
   for(var eq:input.equipamentos()){
     if(eq==null||eq.elevadorId()==null||!unique.add(eq.elevadorId()))throw ApiException.bad("Elevadores duplicados ou invalidos.");
     Elevador e=elevators.findById(eq.elevadorId()).orElseThrow(()->ApiException.notFound("Elevador"));
     if(!e.getPredioId().equals(t.getPredioId()))throw ApiException.bad("Todos os elevadores precisam pertencer ao predio do chamado.");
     if(eq.tiposVisita()==null||eq.tiposVisita().isEmpty()||eq.tiposVisita().contains(null)||!VISITAS.containsAll(eq.tiposVisita())||new HashSet<>(eq.tiposVisita()).size()!=eq.tiposVisita().size())throw ApiException.bad("Tipo de visita invalido.");
     if(eq.situacoes()==null||eq.situacoes().isEmpty()||eq.situacoes().contains(null)||!SITUACOES.containsAll(eq.situacoes())||new HashSet<>(eq.situacoes()).size()!=eq.situacoes().size())throw ApiException.bad("Situacao apos atendimento invalida.");
     if(eq.situacoes().contains("Atendimento regularizado")&&eq.situacoes().size()!=1)throw ApiException.bad("Regularizado nao pode coexistir com pendencias.");
     if(eq.situacoes().contains("Outros")&&(eq.outros()==null||eq.outros().isBlank()))throw ApiException.bad("Descreva Outros.");
     if(eq.tiposVisita().contains("Mensal")){monthly=true;MonthlyRules.validate(eq.checklistMensal());}
     else if(eq.checklistMensal()!=null)throw ApiException.bad("Checklist mensal so pode aparecer em visita mensal.");
   }
   if(regularizado){
     if(input.servicoExecutado()==null)throw ApiException.bad("Validação do serviço obrigatória para concluir.");
     signature(input.servicoExecutado().assinaturaCliente(),true);
     if(input.servicoExecutado().nomeCliente()==null||input.servicoExecutado().nomeCliente().isBlank())throw ApiException.bad("Identifique quem validou o serviço.");
     if(!Objects.equals(req.assinatura(),input.servicoExecutado().assinaturaCliente()))throw ApiException.bad("Assinatura principal inconsistente.");
   }else if(input.servicoExecutado()!=null){
     // Uma visita pendente não pode declarar serviço aprovado sem assinatura real.
     signature(input.servicoExecutado().assinaturaCliente(),true);
     if(!Objects.equals(req.assinatura(),input.servicoExecutado().assinaturaCliente()))throw ApiException.bad("Assinatura principal inconsistente.");
   }
   if(input.autorizacaoMaterial()!=null){
     if(input.materialNecessario()==null||input.materialNecessario().isBlank())throw ApiException.bad("Material obrigatorio para autorizar fornecimento.");
     if(input.autorizacaoMaterial().nomeCliente()==null||input.autorizacaoMaterial().nomeCliente().isBlank())throw ApiException.bad("Nome do cliente obrigatorio na autorizacao.");
     signature(input.autorizacaoMaterial().assinaturaCliente(),true);
   }
   if(monthly){
     if(input.assinaturasMensais()==null)throw ApiException.bad("Assinaturas mensais obrigatorias.");
     signature(input.assinaturasMensais().assinaturaTecnico(),true);
     boolean name=input.assinaturasMensais().nomeLiderServicos()!=null&&!input.assinaturasMensais().nomeLiderServicos().isBlank();
     boolean sign=input.assinaturasMensais().assinaturaLiderServicos()!=null&&!input.assinaturasMensais().assinaturaLiderServicos().isBlank();
     if(name!=sign)throw ApiException.bad("Informe nome e assinatura do lider juntos.");
     signature(input.assinaturasMensais().assinaturaLiderServicos(),false);
   }
   return new RegistroAtendimento(Ids.next("RA"),input.equipamentos(),input.observacoesEmpresa(),input.ocorrencia(),input.materialNecessario(),input.autorizacaoMaterial(),input.servicoExecutado(),input.assinaturasMensais());
 }
}
