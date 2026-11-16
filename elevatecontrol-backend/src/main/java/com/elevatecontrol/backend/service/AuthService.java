package com.elevatecontrol.backend.service;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.domain.*;
import com.elevatecontrol.backend.repository.*;
import com.elevatecontrol.backend.security.ScopeService;
import com.elevatecontrol.backend.support.*;
import jakarta.servlet.http.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class AuthService {
 private final AuthenticationManager manager; private final UsuarioRepository users;private final ScopeService scope;
 private final PasswordEncoder encoder;private final TokenRecuperacaoRepository tokens;
 private final ObjectProvider<JavaMailSender> mailer;
 @Value("${app.frontend-reset-url}") private String resetUrl;
 @Value("${spring.mail.host:}") private String smtpHost;
 public AuthService(AuthenticationManager m,UsuarioRepository u,ScopeService s,PasswordEncoder p,TokenRecuperacaoRepository t,ObjectProvider<JavaMailSender> mailer){
   manager=m;users=u;scope=s;encoder=p;tokens=t;this.mailer=mailer;
 }
 @Transactional(readOnly=true) public UsuarioDto me(){return Mapper.user(scope.current());}
 public UsuarioDto login(Credenciais input,HttpServletRequest request,HttpServletResponse response){
   var authentication=manager.authenticate(new UsernamePasswordAuthenticationToken(input.email().trim(),input.password()));
   var context=SecurityContextHolder.createEmptyContext();context.setAuthentication(authentication);
   SecurityContextHolder.setContext(context);
   if(request.getSession(false)!=null)request.changeSessionId();
   request.getSession(true);
   new HttpSessionSecurityContextRepository().saveContext(context,request,response);
   return Mapper.user(scope.current());
 }
 public void logout(HttpServletRequest request){
   if(request.getSession(false)!=null)request.getSession(false).invalidate();
   SecurityContextHolder.clearContext();
 }
 @Transactional
 public void forgotPassword(String email){
   // Em producao, sem SMTP configurado retornamos indisponibilidade em vez de afirmar que o e-mail foi enviado.
   var sender=mailer.getIfAvailable();
   if(sender==null||smtpHost.isBlank())throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE,"Recuperacao indisponivel: SMTP nao configurado.");
   var user=users.findByEmailIgnoreCase(email.trim());
   if(user.isEmpty())return; // evita enumeracao de usuarios
   byte[] random=new byte[32];new SecureRandom().nextBytes(random);
   String token=Base64.getUrlEncoder().withoutPadding().encodeToString(random);
   TokenRecuperacao t=new TokenRecuperacao();t.setId(Ids.next("RST"));t.setUsuarioId(user.get().getId());t.setTokenHash(sha(token));t.setExpiraEm(LocalDateTime.now().plusMinutes(30));tokens.save(t);
   var msg=new SimpleMailMessage();msg.setTo(user.get().getEmail());msg.setSubject("ElevateControl - recuperar senha");
   msg.setText("Acesse este link em ate 30 minutos: "+resetUrl+(resetUrl.contains("?")?"&":"?")+"token="+token);
   sender.send(msg);
 }
 @Transactional
 public void resetPassword(RedefinirSenha request){
   if(request.novaSenha()==null||request.novaSenha().length()<12)throw ApiException.bad("Senha deve ter ao menos 12 caracteres.");
   var token=tokens.findByTokenHash(sha(request.token())).orElseThrow(()->ApiException.bad("Token invalido ou expirado."));
   if(token.getUsadoEm()!=null || token.getExpiraEm().isBefore(LocalDateTime.now()))throw ApiException.bad("Token expirado ou utilizado.");
   Usuario u=users.findById(token.getUsuarioId()).orElseThrow(()->ApiException.notFound("Usuario"));
   u.setSenhaHash(encoder.encode(request.novaSenha()));u.setTrocaSenhaObrigatoria(false);users.save(u);token.setUsadoEm(LocalDateTime.now());tokens.save(token);
 }
 @Transactional public void changePassword(AlterarSenha req){
   var u=scope.current();
   if(req.novaSenha()==null||req.novaSenha().length()<12)throw ApiException.bad("Senha deve ter ao menos 12 caracteres.");
   if(!encoder.matches(req.senhaAtual(),u.getSenhaHash()))throw ApiException.bad("Senha atual incorreta.");
   if(encoder.matches(req.novaSenha(),u.getSenhaHash()))throw ApiException.bad("Escolha uma senha diferente da atual.");
   u.setSenhaHash(encoder.encode(req.novaSenha()));u.setTrocaSenhaObrigatoria(false);users.save(u);
 }
 @Transactional public UsuarioDto completeSetup(ConfiguracaoInicial req){
   var u=scope.current();
   if(!u.getConfiguracaoInicialPendente() || !scope.admin(u))throw ApiException.forbidden();
   if(!encoder.matches(req.senhaTemporaria(),u.getSenhaHash()))throw ApiException.bad("Senha temporária incorreta.");
   String email=req.email().trim().toLowerCase(Locale.ROOT);
   if(email.isBlank()||req.nome().isBlank())throw ApiException.bad("Nome e e-mail são obrigatórios.");
   if(req.novaSenha().equals(req.senhaTemporaria()))throw ApiException.bad("A nova senha deve ser diferente da temporária.");
   users.findByEmailIgnoreCase(email).ifPresent(other->{if(!other.getId().equals(u.getId()))throw ApiException.conflict("E-mail já cadastrado.");});
   u.setNome(req.nome().trim());u.setEmail(email);u.setSenhaHash(encoder.encode(req.novaSenha()));
   u.setTrocaSenhaObrigatoria(false);u.setConfiguracaoInicialPendente(false);users.save(u);
   return Mapper.user(u);
 }
 @Transactional public UsuarioDto updateProfile(PerfilUsuario req){
   var u=scope.current();
   if(!encoder.matches(req.senhaAtual(),u.getSenhaHash()))throw ApiException.bad("Senha atual incorreta.");
   if(req.nome()==null||req.nome().isBlank())throw ApiException.bad("Informe o nome.");
   if(req.email()==null||req.email().isBlank())throw ApiException.bad("Informe o e-mail.");
   String email=req.email().trim().toLowerCase(Locale.ROOT);
   users.findByEmailIgnoreCase(email).ifPresent(other->{if(!other.getId().equals(u.getId()))throw ApiException.conflict("E-mail já cadastrado.");});
   u.setNome(req.nome().trim());u.setEmail(email);users.save(u);return Mapper.user(u);
 }
 private static String sha(String raw){try{
   byte[] d=MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
   return HexFormat.of().formatHex(d);
 }catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
}
