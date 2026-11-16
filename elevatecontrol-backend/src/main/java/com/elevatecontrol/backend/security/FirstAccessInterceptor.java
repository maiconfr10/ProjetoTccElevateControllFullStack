package com.elevatecontrol.backend.security;

import com.elevatecontrol.backend.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.nio.charset.StandardCharsets;

@Component
public class FirstAccessInterceptor implements HandlerInterceptor {
 private final UsuarioRepository users;
 public FirstAccessInterceptor(UsuarioRepository users){this.users=users;}
 @Override public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler)throws Exception{
   var auth=SecurityContextHolder.getContext().getAuthentication();
   if(auth==null||!auth.isAuthenticated()||"anonymousUser".equals(auth.getPrincipal()))return true;
   var user=users.findByEmailIgnoreCase(auth.getName()).orElse(null);
   if(user==null||(!user.getTrocaSenhaObrigatoria()&&!user.getConfiguracaoInicialPendente()))return true;
   response.setStatus(403);response.setCharacterEncoding(StandardCharsets.UTF_8.name());response.setContentType(MediaType.APPLICATION_JSON_VALUE);
   response.getWriter().write("{\"message\":\"Conclua a configuração obrigatória de acesso.\",\"status\":403}");
   return false;
 }
}
