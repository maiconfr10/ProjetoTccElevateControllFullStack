package com.elevatecontrol.backend.support;
import com.elevatecontrol.backend.domain.*;
import com.elevatecontrol.backend.api.ApiModels.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
public final class Mapper {
  private Mapper() {}
  public static UsuarioDto user(Usuario u) {return new UsuarioDto(u.getId(),u.getNome(),u.getEmail(),u.getPerfil(),u.getRegistro(),u.getClienteId(),u.getTrocaSenhaObrigatoria(),u.getConfiguracaoInicialPendente());}
  public static ClienteDto client(Cliente v) {return new ClienteDto(v.getId(),v.getNome(),v.getDocumento(),v.getContato(),v.getEmail(),v.getTelefone());}
  public static PredioDto building(Predio v) {return new PredioDto(v.getId(),v.getClienteId(),v.getNome(),v.getEndereco());}
  public static ElevadorDto elevator(Elevador v) {return new ElevadorDto(v.getId(),v.getPredioId(),v.getIdentificacao(),v.getMarca(),v.getModelo(),v.getStatus(),v.getPendencia());}
  public static ChamadoDto ticket(Chamado v) {return new ChamadoDto(v.getId(),v.getTitulo(),v.getDescricao(),v.getPredioId(),v.getElevadorId(),v.getTipo(),v.getPrioridade(),v.getStatus(),v.getTecnicoId(),v.getCriadoPor(),dt(v.getData()),dt(v.getConcluidoEm()));}
  public static String dt(LocalDateTime v){return v==null?null:v.toString();}
  public static ChecklistDto checklist(Checklist v,ObjectMapper mapper){
    RegistroAtendimento r=null;
    if(v.getRegistroAtendimentoJson()!=null && !v.getRegistroAtendimentoJson().isBlank()) {
      try{r=mapper.readValue(v.getRegistroAtendimentoJson(),RegistroAtendimento.class);}catch(Exception e){throw new IllegalStateException("Dados de visita armazenados invalidos",e);}
    }
    return new ChecklistDto(v.getId(),v.getChamadoId(),v.getTecnicoId(),v.getTipo(),dt(v.getInicio()),dt(v.getFim()),v.getLubrificacao(),v.getTesteSeguranca(),v.getPecas(),v.getObservacoes(),v.getResponsavel(),v.getAssinatura(),dt(v.getConcluidoEm()),r);
  }
}
