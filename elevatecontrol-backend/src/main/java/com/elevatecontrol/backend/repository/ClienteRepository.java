package com.elevatecontrol.backend.repository;
import com.elevatecontrol.backend.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ClienteRepository extends JpaRepository<Cliente,String> {
    boolean existsByDocumento(String documento);
}
