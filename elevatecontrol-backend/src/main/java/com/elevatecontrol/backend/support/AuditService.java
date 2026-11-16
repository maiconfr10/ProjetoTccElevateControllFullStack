package com.elevatecontrol.backend.support;
import com.elevatecontrol.backend.domain.*;
import com.elevatecontrol.backend.repository.AuditoriaRepository;
import com.elevatecontrol.backend.security.ScopeService;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
@Service
public class AuditService {
 private final AuditoriaRepository repo;private final ScopeService scope;
 public AuditService(AuditoriaRepository r,ScopeService s){repo=r;scope=s;}
 public void record(String action,String resource,String id){
   var v=new Auditoria();v.setId(Ids.next("AUD"));v.setUsuarioId(scope.current().getId());v.setAcao(action);v.setRecurso(resource);v.setRecursoId(id);v.setQuando(LocalDateTime.now());repo.save(v);
 }
}
