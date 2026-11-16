package com.elevatecontrol.backend.repository;
import com.elevatecontrol.backend.domain.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ChecklistRepository extends JpaRepository<Checklist,String> {
    List<Checklist> findByChamadoId(String chamadoId); List<Checklist> findByTecnicoId(String tecnicoId); List<Checklist> findByChamadoIdIn(Collection<String> ids);
}
