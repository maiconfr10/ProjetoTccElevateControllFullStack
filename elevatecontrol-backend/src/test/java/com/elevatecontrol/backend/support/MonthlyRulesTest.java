package com.elevatecontrol.backend.support;
import com.elevatecontrol.backend.api.ApiModels.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class MonthlyRulesTest {
 @Test void catalogoTem29CodigosSemDuplicidade(){
   assertEquals(29,MonthlyCatalog.ROTINAS.size());
   assertEquals(29,MonthlyCatalog.CODIGOS.size());
 }
 @Test void aceitaTodasAsRespostas(){
   var answers=MonthlyCatalog.CODIGOS.stream().map(code->new MarcacaoRotina(code,"NA")).toList();
   assertDoesNotThrow(()->MonthlyRules.validate(new ChecklistMensalDetalhado("2026","",answers,"","","")));
 }
 @Test void rejeitaRotinaAusenteOuDuplicada(){
   var answers=MonthlyCatalog.CODIGOS.stream().limit(28).map(code->new MarcacaoRotina(code,"S")).toList();
   assertThrows(ApiException.class,()->MonthlyRules.validate(new ChecklistMensalDetalhado("2026","",answers,"","","")));
 }
 @Test void naoInterpretaNAComoFalha(){
   var answers=MonthlyCatalog.CODIGOS.stream().map(code->new MarcacaoRotina(code,"NA")).toList();
   assertDoesNotThrow(()->MonthlyRules.validate(new ChecklistMensalDetalhado("2026","",answers,"","","")));
 }
}
