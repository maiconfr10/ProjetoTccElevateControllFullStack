package com.elevatecontrol.backend.service;

import com.elevatecontrol.backend.api.ApiModels.*;
import com.elevatecontrol.backend.domain.*;
import com.elevatecontrol.backend.repository.*;
import com.elevatecontrol.backend.security.ScopeService;
import com.elevatecontrol.backend.support.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;

class ChecklistV07Test {
  @Test void pendenciaPodeSerRegistradaSemAssinaturaDeAprovacao(){
    ChecklistRepository checklistRepo=mock(ChecklistRepository.class);
    ChamadoRepository chamadoRepo=mock(ChamadoRepository.class);
    ElevadorRepository elevadorRepo=mock(ElevadorRepository.class);
    ScopeService scope=mock(ScopeService.class);
    AuditService audit=mock(AuditService.class);
    ChecklistService service=new ChecklistService(checklistRepo,chamadoRepo,elevadorRepo,scope,audit,new ObjectMapper());
    Chamado ticket=new Chamado();ticket.setId("CH-1");ticket.setPredioId("PRD-1");ticket.setElevadorId("ELV-1");ticket.setTecnicoId("USR-2");ticket.setStatus("Em andamento");
    Elevador elevator=new Elevador();elevator.setId("ELV-1");elevator.setPredioId("PRD-1");elevator.setStatus("Operacional");
    when(chamadoRepo.findById("CH-1")).thenReturn(Optional.of(ticket));
    when(elevadorRepo.findById("ELV-1")).thenReturn(Optional.of(elevator));
    RegistroElevador eq=new RegistroElevador("ELV-1",List.of("Corretiva"),List.of("Pendência de Material"),"",null);
    RegistroAtendimento atendimento=new RegistroAtendimento(null,List.of(eq),"Visita pendente","Ocorrencia","Oleo necessario",null,null,null);
    CriarChecklist req=new CriarChecklist("CH-1","USR-2","Atendimento","2026-11-16T09:00","2026-11-16T09:30",false,false,"", "Registro sem servico aprovado","",null,atendimento);
    ChecklistDto saved=service.register(req);
    assertEquals("Em andamento",ticket.getStatus());
    assertEquals("Em manutenção",elevator.getStatus());
    assertEquals("Pendente de validação",saved.responsavel());
    assertNull(saved.assinatura());
    assertNull(saved.registroAtendimento().servicoExecutado());
    verify(checklistRepo).save(any(Checklist.class));
  }

  @Test void conclusaoSemAssinaturaDeveFalhar(){
    ChecklistRepository checklists=mock(ChecklistRepository.class);
    ChamadoRepository tickets=mock(ChamadoRepository.class);
    ElevadorRepository elevators=mock(ElevadorRepository.class);
    ChecklistService service=new ChecklistService(checklists,tickets,elevators,mock(ScopeService.class),mock(AuditService.class),new ObjectMapper());
    Chamado ticket=new Chamado();ticket.setId("CH-1");ticket.setTecnicoId("USR-2");ticket.setElevadorId("ELV-1");ticket.setStatus("Em andamento");
    when(tickets.findById("CH-1")).thenReturn(Optional.of(ticket));
    RegistroElevador eq=new RegistroElevador("ELV-1",List.of("Corretiva"),List.of("Atendimento regularizado"),"",null);
    RegistroAtendimento registro=new RegistroAtendimento(null,List.of(eq),"", "", "",null,null,null);
    CriarChecklist req=new CriarChecklist("CH-1","USR-2","Atendimento","2026-11-16T09:00","2026-11-16T09:30",false,false,"","Servico", "",null,registro);
    assertThrows(ApiException.class,()->service.register(req));
    verify(checklists,never()).save(any());
  }
}
