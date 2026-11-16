package com.elevatecontrol.backend.security;
import com.elevatecontrol.backend.domain.Usuario;
import com.elevatecontrol.backend.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.io.*;
import java.util.*;

class FirstAccessInterceptorV07Test {
 @AfterEach void clear(){SecurityContextHolder.clearContext();}
 @Test void bloqueiaAcessoOperacionalQuandoSenhaTemporaria(){
   var users=mock(UsuarioRepository.class);
   var user=new Usuario();user.setEmail("novo@exemplo.com");user.setTrocaSenhaObrigatoria(true);
   when(users.findByEmailIgnoreCase("novo@exemplo.com")).thenReturn(Optional.of(user));
   SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("novo@exemplo.com","",
     List.of(new SimpleGrantedAuthority("ROLE_TECNICO"))));
   var req=mock(HttpServletRequest.class);var response=mock(HttpServletResponse.class);
   var out=new StringWriter();
   try{when(response.getWriter()).thenReturn(new PrintWriter(out));
       assertFalse(new FirstAccessInterceptor(users).preHandle(req,response,new Object()));}
   catch(Exception ex){throw new AssertionError(ex);}
   verify(response).setStatus(403);
   assertTrue(out.toString().contains("configura"));
 }
}
