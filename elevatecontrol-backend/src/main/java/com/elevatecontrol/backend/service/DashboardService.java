package com.elevatecontrol.backend.service;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.domain.*;
import com.elevatecontrol.backend.repository.*;
import com.elevatecontrol.backend.security.ScopeService;
import com.elevatecontrol.backend.support.Mapper;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class DashboardService {
 private final ChamadoRepository tickets;private final ElevadorRepository elevators;private final ChecklistRepository checklists;private final ScopeService scope;
 public DashboardService(ChamadoRepository t,ElevadorRepository e,ChecklistRepository c,ScopeService s){tickets=t;elevators=e;checklists=c;scope=s;}
 @Transactional(readOnly=true) public ResumoDashboard get(){
   var user=scope.current();Set<String> ids=scope.buildingIds(user);
   List<Chamado> visible=scope.admin(user)?tickets.findAll():scope.tecnico(user)?tickets.findByTecnicoId(user.getId()):tickets.findByPredioIdIn(ids);
   List<Elevador> ev=elevators.findAll().stream().filter(x->ids.contains(x.getPredioId())).toList();
   Set<String> ticketIds=new HashSet<>();
   if(scope.tecnico(user)){
      // Tecnicos podem consultar historicos dos predios nos quais ja atenderam, nao os chamados de outros tecnicos.
      tickets.findByPredioIdIn(scope.visitedBuildingIds(user)).forEach(t->ticketIds.add(t.getId()));
   }else visible.forEach(t->ticketIds.add(t.getId()));
   int checks=(int)checklists.findAll().stream().filter(c->ticketIds.contains(c.getChamadoId())).count();
   return new ResumoDashboard((int)visible.stream().filter(t->"Aberto".equals(t.getStatus())).count(),
     (int)visible.stream().filter(t->"Em andamento".equals(t.getStatus())).count(),
     (int)visible.stream().filter(t->"Concluído".equals(t.getStatus())).count(),
     ev.stream().filter(e->"Fora de serviço".equals(e.getStatus())).count(),
     ev.stream().filter(e->"Em manutenção".equals(e.getStatus())).count(),checks,
     visible.stream().sorted(Comparator.comparing(Chamado::getData).reversed()).limit(5).map(Mapper::ticket).toList());
 }
}
