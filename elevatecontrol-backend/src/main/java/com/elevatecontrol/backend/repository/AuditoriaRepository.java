package com.elevatecontrol.backend.repository;
import com.elevatecontrol.backend.domain.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface AuditoriaRepository extends JpaRepository<Auditoria,String> {
    List<Auditoria> findTop100ByOrderByQuandoDesc();
}
