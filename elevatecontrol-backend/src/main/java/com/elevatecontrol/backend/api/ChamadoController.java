package com.elevatecontrol.backend.api;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.service.ChamadoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/chamados")
public class ChamadoController {
 private final ChamadoService service;
 public ChamadoController(ChamadoService s){service=s;}
 @GetMapping public List<ChamadoDto> list(@RequestParam(required=false)String status,@RequestParam(required=false)String busca){return service.list(status,busca);}
 @GetMapping("/{id}")public ChamadoDto get(@PathVariable String id){return service.get(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED)public ChamadoDto create(@Valid @RequestBody CriarChamado req){return service.create(req);}
 @PatchMapping("/{id}/atribuicao")public ChamadoDto assign(@PathVariable String id,@Valid @RequestBody Atribuicao req){return service.assign(id,req.tecnicoId());}
 @PostMapping("/{id}/iniciar")public ChamadoDto start(@PathVariable String id){return service.start(id);}
 @PatchMapping("/{id}/status")public ChamadoDto status(@PathVariable String id,@Valid @RequestBody AlterarStatus req){return service.changeStatus(id,req.status());}
}
