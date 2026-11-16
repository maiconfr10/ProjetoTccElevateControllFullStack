package com.elevatecontrol.backend.support;
import java.time.Instant;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
@RestControllerAdvice
public class RestErrors {
    private ResponseEntity<Map<String,Object>> fail(HttpStatus status,String message){
        return ResponseEntity.status(status).body(Map.of("timestamp", Instant.now().toString(),"status",status.value(),"message",message));
    }
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String,Object>> api(ApiException ex){return fail(ex.getStatus(),ex.getMessage());}
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> invalid(MethodArgumentNotValidException ex){
        String msg=ex.getBindingResult().getFieldErrors().stream().findFirst().map(x->x.getField()+": "+x.getDefaultMessage()).orElse("Dados invalidos");
        return fail(HttpStatus.BAD_REQUEST,msg);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class,IllegalArgumentException.class})
    public ResponseEntity<Map<String,Object>> invalid(Exception ex){return fail(HttpStatus.BAD_REQUEST,"Dados invalidos ou formato JSON incorreto.");}
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String,Object>> unauthorized(AuthenticationException ex){return fail(HttpStatus.UNAUTHORIZED,"E-mail ou senha invalidos.");}
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,Object>> denied(AccessDeniedException ex){return fail(HttpStatus.FORBIDDEN,"Acesso nao autorizado.");}
}
