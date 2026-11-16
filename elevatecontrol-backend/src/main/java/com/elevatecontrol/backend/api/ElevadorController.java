package com.elevatecontrol.backend.api;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.service.ElevadorService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/elevadores")
public class ElevadorController {
 private final ElevadorService service;
 public ElevadorController(ElevadorService s){service=s;}
 @GetMapping public List<ElevadorDto> list(){return service.list();}
 @GetMapping("/{id}")public ElevadorDto get(@PathVariable String id){return service.get(id);}
 @GetMapping(params="predioId")public List<ElevadorDto> byBuilding(@RequestParam String predioId){return service.byBuilding(predioId);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED)public ElevadorDto create(@Valid @RequestBody CriarElevador req){return service.create(req);}
 @PatchMapping("/{id}")public ElevadorDto update(@PathVariable String id,@RequestBody AtualizarElevador req){return service.update(id,req);}
}
