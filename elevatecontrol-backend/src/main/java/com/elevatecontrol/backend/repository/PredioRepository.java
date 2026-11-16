package com.elevatecontrol.backend.repository;
import com.elevatecontrol.backend.domain.Predio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PredioRepository extends JpaRepository<Predio,String> {
    List<Predio> findByClienteId(String clienteId);
}
