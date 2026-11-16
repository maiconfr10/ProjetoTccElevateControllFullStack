package com.elevatecontrol.backend.security;
import com.elevatecontrol.backend.domain.*;
import com.elevatecontrol.backend.repository.*;
import com.elevatecontrol.backend.support.ApiException;
import java.util.*;
import java.util.stream.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
@Service
public class ScopeService {
  private final UsuarioRepository users;
  private final PredioRepository buildings;
  private final ChamadoRepository tickets;
  private final ChecklistRepository checklists;
  public ScopeService(UsuarioRepository u,PredioRepository b,ChamadoRepository t,ChecklistRepository c){users=u;buildings=b;tickets=t;checklists=c;}
  public Usuario current(){
    var auth=SecurityContextHolder.getContext().getAuthentication();
    if(auth==null||!auth.isAuthenticated()||"anonymousUser".equals(auth.getPrincipal()))throw ApiException.forbidden();
    return users.findByEmailIgnoreCase(auth.getName()).filter(Usuario::getAtivo).orElseThrow(ApiException::forbidden);
  }
  public boolean admin(Usuario u){return "admin".equals(u.getPerfil());}
  public boolean tecnico(Usuario u){return "tecnico".equals(u.getPerfil());}
  public boolean cliente(Usuario u){return "cliente".equals(u.getPerfil());}
  public void requireAdmin(){if(!admin(current()))throw ApiException.forbidden();}
  public Set<String> buildingIds(Usuario u){
    if(admin(u)) return buildings.findAll().stream().map(Predio::getId).collect(Collectors.toSet());
    if(cliente(u))return u.getClienteId()==null?Set.of():buildings.findByClienteId(u.getClienteId()).stream().map(Predio::getId).collect(Collectors.toSet());
    if(tecnico(u)){
      Set<String> ids=new HashSet<>();
      tickets.findByTecnicoId(u.getId()).forEach(t->ids.add(t.getPredioId()));
      for(var v:checklists.findByTecnicoId(u.getId()))tickets.findById(v.getChamadoId()).ifPresent(t->ids.add(t.getPredioId()));
      return ids;
    }
    return Set.of();
  }
  public Set<String> visitedBuildingIds(Usuario u){
    if(!tecnico(u))return buildingIds(u);
    Set<String> visited=new HashSet<>();
    for(var v:checklists.findByTecnicoId(u.getId()))tickets.findById(v.getChamadoId()).ifPresent(t->visited.add(t.getPredioId()));
    return visited;
  }
  public List<Predio> prediosVisiveis(Usuario u){
    Set<String> ids=buildingIds(u);
    return buildings.findAll().stream().filter(b->ids.contains(b.getId())).toList();
  }
  public void canSeeBuilding(String buildingId){if(!buildingIds(current()).contains(buildingId))throw ApiException.forbidden();}
  public void canSeeTicket(Chamado t){
    Usuario u=current();
    if(admin(u))return;
    if(cliente(u) && buildingIds(u).contains(t.getPredioId()))return;
    if(tecnico(u) && u.getId().equals(t.getTecnicoId()))return;
    throw ApiException.forbidden();
  }
  public void canWork(Chamado t){
    var u=current();
    if(admin(u)||tecnico(u)&&u.getId().equals(t.getTecnicoId()))return;
    throw ApiException.forbidden();
  }
  public void canSeeChecklist(Checklist v,Chamado t){
    var u=current();
    if(admin(u)||cliente(u)&&buildingIds(u).contains(t.getPredioId())||tecnico(u)&&visitedBuildingIds(u).contains(t.getPredioId()))return;
    throw ApiException.forbidden();
  }
}
