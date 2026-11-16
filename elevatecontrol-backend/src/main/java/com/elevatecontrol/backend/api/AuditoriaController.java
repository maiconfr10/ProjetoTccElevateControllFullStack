package com.elevatecontrol.backend.api;
import com.elevatecontrol.backend.domain.Auditoria;
import com.elevatecontrol.backend.repository.AuditoriaRepository;
import com.elevatecontrol.backend.security.ScopeService;
import java.util.List;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auditoria")
public class AuditoriaController {
 private final AuditoriaRepository repo;private final ScopeService scope;
 public AuditoriaController(AuditoriaRepository r,ScopeService s){repo=r;scope=s;}
 @GetMapping public List<Auditoria> list(){scope.requireAdmin();return repo.findTop100ByOrderByQuandoDesc();}
}
