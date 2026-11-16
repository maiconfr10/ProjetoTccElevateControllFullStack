package com.elevatecontrol.backend.repository;
import com.elevatecontrol.backend.domain.TokenRecuperacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface TokenRecuperacaoRepository extends JpaRepository<TokenRecuperacao,String> {
    Optional<TokenRecuperacao> findByTokenHash(String tokenHash);
}
