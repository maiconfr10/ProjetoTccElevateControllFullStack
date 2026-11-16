# ElevateControl - Contratos REST (Spring Boot)

Base: `http://localhost:8080/api`. Todos os objetos JSON preservam nomes e enumerações do frontend `v0.5`.

## Autenticação / sessão

| Método | Caminho | Perfil | Descrição |
|---|---|---|---|
| GET | `/auth/csrf` | Público | Token CSRF; sempre chamar antes do primeiro POST |
| POST | `/auth/login` | Público + CSRF | Recebe `{ "email": "...", "password": "..." }` e devolve `{id,nome,email,perfil,registro,clienteId}` |
| GET | `/auth/me` | Autenticado | Perfil público da sessão |
| POST | `/auth/logout` | Autenticado | Invalida a sessão |
| POST | `/auth/forgot-password` | Público | Solicita token via SMTP (503 se SMTP não estiver configurado) |
| POST | `/auth/reset-password` | Público | `{token,novaSenha}` (mín. 12 caracteres) |
| POST | `/auth/change-password` | Autenticado | `{senhaAtual,novaSenha}` |

A API usa cookie de sessão HttpOnly `JSESSIONID`, mais `XSRF-TOKEN` e cabeçalho `X-XSRF-TOKEN` para métodos que mudam dados. Não use tokens de sessão em `localStorage`.

## Domínio

| Método | Caminho | Perfis | Descrição |
|---|---|---|---|
| GET | `/clientes` | Admin | Listar clientes |
| POST | `/clientes` | Admin | Cadastrar cliente |
| GET | `/clientes/{id}` | Admin | Detalhar cliente |
| GET | `/predios` | Todos (escopo) | Listar prédios permitidos |
| POST | `/predios` | Admin | Cadastrar prédio |
| GET | `/predios/{id}` | Todos (escopo) | Detalhar prédio |
| GET | `/elevadores` | Todos (escopo) | Listar elevadores permitidos |
| GET | `/elevadores?predioId={id}` | Todos (escopo) | Listar elevadores de um prédio |
| GET | `/elevadores/{id}` | Todos (escopo) | Detalhar elevador |
| POST | `/elevadores` | Admin | Cadastrar elevador |
| PATCH | `/elevadores/{id}` | Admin | Atualizar elevador |
| GET | `/usuarios` | Admin | Listar usuários |
| POST | `/usuarios` | Admin | Cadastrar usuário; `senhaTemporaria` só na resposta de criação |
| GET | `/usuarios/{id}` | Admin | Detalhar usuário |
| GET | `/chamados` | Todos (escopo) | Listar chamados; `status` e `busca` opcionais |
| POST | `/chamados` | Admin/Cliente | Abrir chamado; emergência força prioridade Crítica |
| GET | `/chamados/{id}` | Todos (escopo) | Detalhar chamado |
| PATCH | `/chamados/{id}/atribuicao` | Admin | Atribuir técnico |
| POST | `/chamados/{id}/iniciar` | Admin/Técnico autorizado | Iniciar chamado |
| PATCH | `/chamados/{id}/status` | Admin | Reabrir/colocar em andamento; não conclui sem visita |
| GET | `/chamados/{id}/visitas` | Todos (escopo/histórico) | Visitas associadas |
| GET | `/checklists` | Todos (histórico) | Checklists por perfil; filtro `tipo` opcional |
| POST | `/checklists` | Admin/Técnico autorizado | Salvar visita + alterar status em transação |
| GET | `/checklists/{id}` | Todos (histórico) | Detalhar checklist |
| GET | `/checklists/catalogo/mensal-2026` | Autenticado | Catálogo de 29 perguntas |
| GET | `/relatorios` | Todos (histórico) | Listar relatórios |
| GET | `/relatorios/{id}` | Todos (histórico) | Detalhes |
| GET | `/relatorios/{id}/pdf` | Todos (histórico) | Download PDF |
| GET | `/dashboard/resumo` | Todos (escopo) | Contadores e chamados recentes |
| GET | `/auditoria` | Admin | Últimos 100 eventos |
| GET | `/actuator/health` | Público | Saúde da aplicação |

## Contratos idênticos ao React

- **Perfis:** `admin`, `tecnico`, `cliente`.
- **Status do chamado:** `Aberto`, `Em andamento`, `Concluído`.
- **Tipo do chamado:** `Corretiva`, `Preventiva`, `Emergência`.
- **Prioridade:** `Normal`, `Alta`, `Crítica`.
- **Status do elevador:** `Operacional`, `Em manutenção`, `Fora de serviço`.
- **Tipos de visita:** `Mensal`, `Auditoria`, `Corretiva`, `Vistoria`.
- **Situações do atendimento:** `Atendimento regularizado`, `Pendência de Material`, `Solicitação de Apoio Técnico`, `Orçamento pendente de aprovação`, `Parado`, `Outros`.
- **Resposta das 29 rotinas:** `S`, `N`, `NA`.

O campo `ll` é mantido como string, com significado técnico pendente de confirmação pelo grupo.

## Exemplo: criar chamado

```json
{
  "titulo":"Elevador com falha na porta",
  "descricao":"Porta do elevador não abre no terceiro andar",
  "predioId":"PRD-01",
  "elevadorId":"ELV-01",
  "tipo":"Corretiva",
  "prioridade":"Alta",
  "tecnicoId":null
}
```

## Exemplo: cadastro de técnico

```json
{
  "nome":"Novo Técnico",
  "email":"tecnico@exemplo.com",
  "perfil":"tecnico",
  "registro":"TEC-888",
  "clienteId":null
}
```

`senhaInicial` pode ser enviada pelo admin (mínimo 12 caracteres) ou omitida, caso em que a API gera uma senha temporária segura, retornada somente uma vez.

## Exemplo: registrar visita corretiva

> Assinaturas nos exemplos abaixo são marcadores: para usar a API é necessário enviar a imagem real gerada por `SignaturePad` em `data:image/png;base64,...`.

```json
{
  "chamadoId":"CH-1001",
  "tecnicoId":"USR-02",
  "tipo":"Atendimento",
  "inicio":"2026-11-15T08:00",
  "fim":"2026-11-15T09:20",
  "lubrificacao":false,
  "testeSeguranca":true,
  "pecas":"Sensor de porta",
  "observacoes":"Teste de operação após troca do sensor",
  "responsavel":"Responsável do condomínio",
  "assinatura":"data:image/png;base64,<IMAGEM_REAL>",
  "registroAtendimento":{
    "numeroRegistro":"RA-IGNORADO-PELO-SERVIDOR",
    "equipamentos":[{
      "elevadorId":"ELV-02",
      "tiposVisita":["Corretiva"],
      "situacoes":["Atendimento regularizado"],
      "outros":""
    }],
    "observacoesEmpresa":"Registro de manutenção corretiva",
    "ocorrencia":"Sensor com falha",
    "materialNecessario":"",
    "autorizacaoMaterial":null,
    "servicoExecutado":{
      "data":"2026-11-15",
      "nomeCliente":"Responsável do condomínio",
      "tecnico":"Rafael Oliveira",
      "assinaturaCliente":"data:image/png;base64,<MESMA_IMAGEM_REAL>"
    }
  }
}
```

Nota: o servidor emite o `numeroRegistro` real, verifica assinaturas, valida os elevadores, mantém pendências em aberto e não usa IA para decidir sobre segurança do equipamento.
