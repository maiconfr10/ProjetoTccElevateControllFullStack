package com.elevatecontrol.backend.repository;
import com.elevatecontrol.backend.domain.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ChamadoRepository extends JpaRepository<Chamado,String> {
    List<Chamado> findByPredioIdIn(Collection<String> ids); List<Chamado> findByTecnicoId(String tecnicoId); List<Chamado> findByStatus(String status);
}
