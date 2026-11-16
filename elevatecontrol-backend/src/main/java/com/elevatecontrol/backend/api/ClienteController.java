package com.elevatecontrol.backend.api;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.service.ClienteService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/clientes")
public class ClienteController {
 private final ClienteService service;
 public ClienteController(ClienteService s){service=s;}
 @GetMapping public List<ClienteDto> list(){return service.list();}
 @GetMapping("/nomes-visiveis") public List<ClienteNome> nomesVisiveis(){return service.nomesVisiveis();}
 @GetMapping("/{id}")public ClienteDto get(@PathVariable String id){return service.get(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public ClienteDto create(@Valid @RequestBody CriarCliente req){return service.create(req);}
}
