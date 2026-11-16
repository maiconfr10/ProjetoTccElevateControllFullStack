package com.elevatecontrol.backend.security;

import com.elevatecontrol.backend.domain.Usuario;
import com.elevatecontrol.backend.repository.UsuarioRepository;
import com.elevatecontrol.backend.support.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

@Configuration
public class BootstrapAdmin {
    private static final Logger log = LoggerFactory.getLogger(BootstrapAdmin.class);

    @Bean
    ApplicationRunner bootstrapAdminRunner(
            UsuarioRepository users,
            PasswordEncoder encoder,
            @Value("${app.bootstrap.email:}") String email,
            @Value("${app.bootstrap.password:}") String password,
            @Value("${app.bootstrap.name:Administrador Inicial}") String name) {
        return args -> {
            // Executado na inicializacao, apos as migracoes Flyway/JPA.
            // Nao recria nem sobrescreve o administrador em reinicializacoes.
            if (!users.findByPerfil("admin").isEmpty()) {
                log.info("Administrador ja cadastrado; bootstrap nao executado.");
                return;
            }
            if (email == null || email.isBlank() || password == null || password.isBlank()) {
                throw new IllegalStateException(
                    "Banco sem administrador: defina BOOTSTRAP_ADMIN_EMAIL e " +
                    "BOOTSTRAP_ADMIN_PASSWORD (12+ caracteres) antes da primeira inicializacao.");
            }
            String normalized = email.trim().toLowerCase(Locale.ROOT);
            if (!normalized.contains("@") || normalized.length() > 180) {
                throw new IllegalStateException("BOOTSTRAP_ADMIN_EMAIL invalido.");
            }
            if (password.length() < 12) {
                throw new IllegalStateException("BOOTSTRAP_ADMIN_PASSWORD precisa de 12 ou mais caracteres.");
            }
            if (users.existsByEmailIgnoreCase(normalized)) {
                throw new IllegalStateException("E-mail de bootstrap pertence a outro usuario.");
            }
            Usuario admin = new Usuario();
            admin.setId(Ids.next("USR"));
            admin.setNome(name == null || name.isBlank() ? "Administrador Inicial" : name.trim());
            admin.setEmail(normalized);
            admin.setSenhaHash(encoder.encode(password));
            admin.setPerfil("admin");
            admin.setAtivo(true);
            admin.setTrocaSenhaObrigatoria(true);
            admin.setConfiguracaoInicialPendente(true);
            users.save(admin);
            log.info("Conta administrativa inicial criada. Configuracao de primeiro acesso obrigatoria.");
            // Nunca registrar a senha nem o hash.
        };
    }
}
