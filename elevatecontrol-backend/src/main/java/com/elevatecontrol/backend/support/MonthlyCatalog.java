package com.elevatecontrol.backend.support;
import java.util.*;
public final class MonthlyCatalog {
  private MonthlyCatalog() {}
  public record Rotina(String codigo,String descricao,String periodicidade) {}
  public static final List<Rotina> ROTINAS=List.of(
    new Rotina("MAQ-01", "Indagar ao porteiro / zelador se existe(m) problema(s)", ""),
    new Rotina("MAQ-02", "Verificar nível de óleo do redutor e mancais do motor", ""),
    new Rotina("MAQ-03", "Verificar limitador de velocidade", ""),
    new Rotina("MAQ-04", "Regular freio e verificar desgastes", ""),
    new Rotina("MAQ-05", "Verificar se existe vazamento no redutor", ""),
    new Rotina("MAQ-06", "Lubrificar todos os pontos que tenham graxeiras", ""),
    new Rotina("MAQ-07", "Limpar redutor de velocidade", ""),
    new Rotina("MAQ-08", "Limpar casa de máquina", ""),
    new Rotina("MAQ-09", "Fusíveis completos (quadro)", ""),
    new Rotina("MAQ-10", "Fusíveis de reserva", ""),
    new Rotina("MAQ-11", "Existe folga no redutor?", "6 em 6 meses"),
    new Rotina("MAQ-12", "Freio de segurança atuou?", "6 em 6 meses"),
    new Rotina("MAQ-13", "O contato do freio de segurança atuou?", "6 em 6 meses"),
    new Rotina("MAQ-14", "O cabo de tração tem quebras?", ""),
    new Rotina("MAQ-15", "Verificar: iluminação / portas", ""),
    new Rotina("MAQ-16", "Verificar: janelas / extintor", ""),
    new Rotina("MAQ-17", "Existem molas de tração quebradas?", ""),
    new Rotina("CAB-01", "Barulho no decorrer da viagem", ""),
    new Rotina("CAB-02", "Portas de andares (reapertar ditadores, fechos e bombas)", ""),
    new Rotina("CAB-03", "Lubrificar guias de cabina e contrapeso", ""),
    new Rotina("CAB-04", "Lubrificar fechos eletromecânicos", ""),
    new Rotina("CAB-05", "Lubrificar dobradiças e molas TS", ""),
    new Rotina("CAB-06", "Existem acrílicos quebrados?", ""),
    new Rotina("CAB-07", "Limpar topo de cabina", ""),
    new Rotina("POCO-01", "Limpar embaixo da cabina", "3 em 3 meses"),
    new Rotina("POCO-02", "Lubrificar tensor e verificar cabos", ""),
    new Rotina("POCO-03", "A distância entre o contrapeso e para-choque é padrão?", "6 em 6 meses"),
    new Rotina("POCO-04", "Para-choque em ordem?", ""),
    new Rotina("POCO-05", "Limpar fundo do poço", "")
  );
  public static final Set<String> CODIGOS=Set.of("MAQ-01","MAQ-02","MAQ-03","MAQ-04","MAQ-05","MAQ-06","MAQ-07","MAQ-08","MAQ-09","MAQ-10","MAQ-11","MAQ-12","MAQ-13","MAQ-14","MAQ-15","MAQ-16","MAQ-17","CAB-01","CAB-02","CAB-03","CAB-04","CAB-05","CAB-06","CAB-07","POCO-01","POCO-02","POCO-03","POCO-04","POCO-05");
}
