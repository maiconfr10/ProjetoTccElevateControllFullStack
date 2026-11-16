package com.elevatecontrol.backend.api;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.service.PredioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/predios")
public class PredioController {
 private final PredioService service;
 public PredioController(PredioService s){service=s;}
 @GetMapping public List<PredioDto> list(){return service.list();}
 @GetMapping("/{id}")public PredioDto get(@PathVariable String id){return service.get(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED)public PredioDto create(@Valid @RequestBody CriarPredio req){return service.create(req);}
}
