package com.elevatecontrol.backend.security;
import com.elevatecontrol.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.*;
import org.springframework.web.cors.*;
import java.util.*;
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder(12);}
  @Bean UserDetailsService userDetailsService(UsuarioRepository users){return email->{
    var user=users.findByEmailIgnoreCase(email).orElseThrow(()->new UsernameNotFoundException("Usuario nao encontrado"));
    return User.withUsername(user.getEmail()).password(user.getSenhaHash())
      .roles(user.getPerfil().toUpperCase(Locale.ROOT)).disabled(!user.getAtivo()).build();
  };}
  @Bean AuthenticationManager authenticationManager(UserDetailsService uds,PasswordEncoder encoder){
    DaoAuthenticationProvider provider=new DaoAuthenticationProvider(uds);
    provider.setPasswordEncoder(encoder);
    return new ProviderManager(provider);
  }
  @Bean CorsConfigurationSource corsConfigurationSource(@Value("${app.frontend-origins}") String origins){
    CorsConfiguration cors=new CorsConfiguration();
    cors.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).toList());
    cors.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
    cors.setAllowedHeaders(List.of("Content-Type","Accept","X-XSRF-TOKEN","X-CSRF-TOKEN"));
    cors.setAllowCredentials(true);
    cors.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**",cors);
    return source;
  }
  @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(c->{}).csrf(csrf->csrf
      .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
      .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
      .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
      .authorizeHttpRequests(auth->auth
        .requestMatchers("/api/auth/login","/api/auth/csrf","/api/auth/forgot-password","/api/auth/reset-password","/actuator/health").permitAll()
        .anyRequest().authenticated())
      .httpBasic(b->b.disable()).formLogin(f->f.disable());
    return http.build();
  }
}
