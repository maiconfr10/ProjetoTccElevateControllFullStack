package com.elevatecontrol.backend.api;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.service.AuthService;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final AuthService service;
 public AuthController(AuthService s){service=s;}
 @GetMapping("/csrf") public Map<String,String> csrf(HttpServletRequest request){
   CsrfToken token=(CsrfToken)request.getAttribute(CsrfToken.class.getName());
   return Map.of("headerName",token.getHeaderName(),"token",token.getToken());
 }
 @PostMapping("/login")public UsuarioDto login(@Valid @RequestBody Credenciais data,HttpServletRequest req,HttpServletResponse resp){return service.login(data,req,resp);}
 @GetMapping("/me")public UsuarioDto me(){return service.me();}
 @PostMapping("/logout")@ResponseStatus(HttpStatus.NO_CONTENT)public void logout(HttpServletRequest req){service.logout(req);}
 @PostMapping("/forgot-password")@ResponseStatus(HttpStatus.ACCEPTED)public void forgot(@Valid @RequestBody SolicitarRecuperacao req){service.forgotPassword(req.email());}
 @PostMapping("/reset-password")@ResponseStatus(HttpStatus.NO_CONTENT)public void reset(@Valid @RequestBody RedefinirSenha req){service.resetPassword(req);}
 @PostMapping("/change-password")@ResponseStatus(HttpStatus.NO_CONTENT)public void change(@Valid @RequestBody AlterarSenha req){service.changePassword(req);}
 @PostMapping("/complete-setup") public UsuarioDto completeSetup(@Valid @RequestBody ConfiguracaoInicial req){return service.completeSetup(req);}
 @PutMapping("/profile") public UsuarioDto updateProfile(@Valid @RequestBody PerfilUsuario req){return service.updateProfile(req);}
}
