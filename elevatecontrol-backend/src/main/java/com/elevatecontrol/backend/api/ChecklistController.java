package com.elevatecontrol.backend.api;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.service.ChecklistService;
import com.elevatecontrol.backend.support.MonthlyCatalog;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/checklists")
public class ChecklistController {
 private final ChecklistService service;
 public ChecklistController(ChecklistService s){service=s;}
 @GetMapping public List<ChecklistDto> list(@RequestParam(required=false)String tipo){return service.list(tipo);}
 @GetMapping("/{id}")public ChecklistDto get(@PathVariable String id){return service.get(id);}
 @GetMapping("/catalogo/mensal-2026")public List<MonthlyCatalog.Rotina> catalog(){return MonthlyCatalog.ROTINAS;}
 @PostMapping @ResponseStatus(HttpStatus.CREATED)public ChecklistDto create(@Valid @RequestBody CriarChecklist req){return service.register(req);}
}
