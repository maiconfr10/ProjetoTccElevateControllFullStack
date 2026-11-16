package com.elevatecontrol.backend.api;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/usuarios")
public class UsuarioController {
 private final UsuarioService service;
 public UsuarioController(UsuarioService s){service=s;}
 @GetMapping public List<UsuarioDto> list(){return service.list();}
 @GetMapping("/{id}")public UsuarioDto get(@PathVariable String id){return service.get(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED)public UsuarioCriado create(@Valid @RequestBody CriarUsuario req){return service.create(req);}
}
