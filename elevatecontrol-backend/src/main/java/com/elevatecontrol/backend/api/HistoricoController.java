package com.elevatecontrol.backend.api;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.service.ChecklistService;
import java.util.List;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/chamados/{id}/visitas")
public class HistoricoController {
 private final ChecklistService service;
 public HistoricoController(ChecklistService s){service=s;}
 @GetMapping public List<ChecklistDto> list(@PathVariable String id){return service.byTicket(id);}
}
