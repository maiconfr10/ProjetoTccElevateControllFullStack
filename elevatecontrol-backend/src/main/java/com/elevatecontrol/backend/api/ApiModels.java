package com.elevatecontrol.backend.api;
import jakarta.validation.constraints.*;
import java.util.*;
public final class ApiModels {
  private ApiModels() {}
  public record Credenciais(@Email @NotBlank String email,@NotBlank String password) {}
  public record SolicitarRecuperacao(@Email @NotBlank String email) {}
  public record RedefinirSenha(@NotBlank String token,@Size(min=12) String novaSenha) {}
  public record ClienteDto(String id,String nome,String documento,String contato,String email,String telefone) {}
  public record CriarCliente(@NotBlank String nome,@NotBlank String documento,@NotBlank String contato,@Email @NotBlank String email,@NotBlank String telefone) {}
  public record PredioDto(String id,String clienteId,String nome,String endereco) {}
  public record CriarPredio(@NotBlank String clienteId,@NotBlank String nome,@NotBlank String endereco) {}
  public record ElevadorDto(String id,String predioId,String identificacao,String marca,String modelo,String status,String pendencia) {}
  public record CriarElevador(@NotBlank String predioId,@NotBlank String identificacao,@NotBlank String marca,@NotBlank String modelo,@NotBlank String status,String pendencia) {}
  public record AtualizarElevador(String predioId,String identificacao,String marca,String modelo,String status,String pendencia) {}
  public record UsuarioDto(String id,String nome,String email,String perfil,String registro,String clienteId,boolean trocaSenhaObrigatoria,boolean configuracaoInicialPendente) {}
  public record PerfilUsuario(@NotBlank String nome,@Email @NotBlank String email,@NotBlank String senhaAtual) {}
  public record ConfiguracaoInicial(@NotBlank String nome,@Email @NotBlank String email,@NotBlank String senhaTemporaria,@Size(min=12) String novaSenha) {}
  public record CriarUsuario(@NotBlank String nome,@Email @NotBlank String email,@NotBlank String perfil,String registro,String clienteId,String senhaInicial) {}
  public record UsuarioCriado(String id,String nome,String email,String perfil,String registro,String clienteId,String senhaTemporaria) {}
  public record AlterarSenha(@NotBlank String senhaAtual,@Size(min=12) String novaSenha) {}
  public record ClienteNome(String id,String nome) {}
  public record ChamadoDto(String id,String titulo,String descricao,String predioId,String elevadorId,String tipo,String prioridade,String status,String tecnicoId,String criadoPor,String data,String concluidoEm) {}
  public record CriarChamado(@NotBlank String titulo,@NotBlank String descricao,@NotBlank String predioId,@NotBlank String elevadorId,@NotBlank String tipo,@NotBlank String prioridade,String tecnicoId) {}
  public record Atribuicao(@NotBlank String tecnicoId) {}
  public record AlterarStatus(@NotBlank String status) {}
  public record MarcacaoRotina(@NotBlank String codigo,@NotBlank String resposta) {}
  public record ChecklistMensalDetalhado(String versao,String ll,List<MarcacaoRotina> respostas,String folgaRedutorMm,String medidasAcrilicos,String distanciaContrapesoParachoqueMm) {}
  public record RegistroElevador(String elevadorId,List<String> tiposVisita,List<String> situacoes,String outros,ChecklistMensalDetalhado checklistMensal) {}
  public record AutorizacaoMaterial(String data,String nomeCliente,String tecnico,String assinaturaCliente) {}
  public record ServicoExecutado(String data,String nomeCliente,String tecnico,String assinaturaCliente) {}
  public record AssinaturasMensais(String assinaturaTecnico,String nomeLiderServicos,String assinaturaLiderServicos) {}
  public record RegistroAtendimento(String numeroRegistro,List<RegistroElevador> equipamentos,String observacoesEmpresa,String ocorrencia,String materialNecessario,AutorizacaoMaterial autorizacaoMaterial,ServicoExecutado servicoExecutado,AssinaturasMensais assinaturasMensais) {}
  public record CriarChecklist(@NotBlank String chamadoId,String tecnicoId,@NotBlank String tipo,@NotBlank String inicio,@NotBlank String fim,Boolean lubrificacao,Boolean testeSeguranca,String pecas,@NotBlank String observacoes,String responsavel,String assinatura,RegistroAtendimento registroAtendimento) {}
  public record ChecklistDto(String id,String chamadoId,String tecnicoId,String tipo,String inicio,String fim,boolean lubrificacao,boolean testeSeguranca,String pecas,String observacoes,String responsavel,String assinatura,String concluidoEm,RegistroAtendimento registroAtendimento) {}
  public record ResumoDashboard(int chamadosAbertos,int chamadosEmAndamento,int chamadosConcluidos,long elevadoresForaDeServico,long elevadoresEmManutencao,int visitasRegistradas, List<ChamadoDto> chamadosRecentes) {}
}
