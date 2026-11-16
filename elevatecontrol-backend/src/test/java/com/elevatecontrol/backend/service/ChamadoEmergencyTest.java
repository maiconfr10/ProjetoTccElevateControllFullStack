package com.elevatecontrol.backend.service;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ChamadoEmergencyTest {
 @Test void reconhecePessoasPresas(){assertTrue(ChamadoService.emergency("Tem pessoas presas dentro do elevador"));}
 @Test void naoClassificaQualquerDescricaoComoEmergencia(){assertFalse(ChamadoService.emergency("Trocar luz interna"));}
}
