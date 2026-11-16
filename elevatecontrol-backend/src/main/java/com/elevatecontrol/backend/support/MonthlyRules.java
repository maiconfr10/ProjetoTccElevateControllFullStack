package com.elevatecontrol.backend.support;
import com.elevatecontrol.backend.api.ApiModels.*;
import java.util.*;
import java.util.stream.*;
public final class MonthlyRules {
  private MonthlyRules() {}
  public static void validate(ChecklistMensalDetalhado data){
    if(data==null||!"2026".equals(data.versao()))throw ApiException.bad("Checklist mensal 2026 obrigatorio.");
    if(data.respostas()==null||data.respostas().size()!=MonthlyCatalog.CODIGOS.size())throw ApiException.bad("Preencha as 29 rotinas mensais.");
    Set<String> found=new HashSet<>();
    for(var answer:data.respostas()){
      if(answer==null||answer.codigo()==null||answer.resposta()==null||!MonthlyCatalog.CODIGOS.contains(answer.codigo())||!Set.of("S","N","NA").contains(answer.resposta())||!found.add(answer.codigo())){
        throw ApiException.bad("Rotinas invalidas, duplicadas ou fora do catalogo 2026.");
      }
    }
    if(!found.equals(MonthlyCatalog.CODIGOS))throw ApiException.bad("O checklist mensal esta incompleto.");
  }
}
