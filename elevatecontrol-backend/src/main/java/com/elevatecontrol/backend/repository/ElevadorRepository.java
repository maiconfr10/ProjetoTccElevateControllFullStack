package com.elevatecontrol.backend.repository;
import com.elevatecontrol.backend.domain.Elevador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ElevadorRepository extends JpaRepository<Elevador,String> {
    List<Elevador> findByPredioId(String predioId);
}
