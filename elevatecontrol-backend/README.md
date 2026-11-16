> **Demonstração TCC / instalação do zero:** leia [README-DEMONSTRACAO.md](README-DEMONSTRACAO.md) antes de apagar ou recriar o banco.

# ElevateControl — Backend Spring Boot

**Backend executável (código-fonte) para o TCC ElevateControl, baseado no frontend React + TypeScript v0.5.**

Este repositório contém a API REST em **Java 21, Spring Boot 3.5.16, Spring Security, Spring Data JPA, Flyway, MySQL e Apache PDFBox**. Abrange login, cadastros, chamados, visitas técnicas, checklist mensal padrão 2026, assinaturas, relatórios e dashboard.

> **Estado da entrega:** código completo para desenvolvimento e testes locais. O projeto foi analisado estruturalmente e seus arquivos Java passaram na verificação de sintaxe, mas **não houve compilação Maven nem execução integrada MySQL neste ambiente** (dependências externas indisponíveis). Execute `mvn clean test` e `mvn clean package` na sua máquina e reporte erros antes de considerá-lo homologado ou publicá-lo em produção. A proteção de sessão, assinaturas e relatórios precisa de auditoria de segurança operacional antes de uso com dados reais.

## 1. Requisitos

- Java 21 (`java -version`)
- Apache Maven 3.6.3+ (`mvn -version`)
- Docker Desktop com Docker Compose **ou** MySQL 8 configurado localmente
- VS Code com *Extension Pack for Java* ou IntelliJ IDEA

## 2. Executar com dados de demonstração (recomendado para começar)

1. Extraia o projeto e abra a pasta `elevatecontrol-backend` no VS Code.
2. No terminal Git Bash, copie as configurações:

```bash
cp .env.example .env
```

3. Inicie o MySQL em contêiner (o servidor fica disponível somente na máquina local):

```bash
docker compose --env-file .env up -d
```

4. Inicie o backend com o perfil de demonstração:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=demo
```

5. Verifique no navegador `http://localhost:8080/actuator/health`.

O Flyway criará as tabelas automaticamente e o perfil `demo` cadastrará clientes, prédios, elevadores, 5 usuários e chamados fictícios. É intencional que **dados reais nunca usem o perfil demo**.

### Contas do perfil demo

| Perfil | E-mail | Senha |
|---|---|---|
| Administrador | `camila@exemplo.com` | `Demo@123` |
| Técnico | `rafael@exemplo.com` | `Demo@123` |
| Técnico | `bruno@exemplo.com` | `Demo@123` |
| Cliente | `mariana@exemplo.com` | `Demo@123` |
| Cliente | `pedro@exemplo.com` | `Demo@123` |

**Atenção:** essas senhas só são introduzidas com `-Dspring-boot.run.profiles=demo`. Nunca use o perfil demo em servidor público. O perfil normal não cria automaticamente contas com senhas fracas.

### Banco já instalado no PC?

Crie o banco vazio `elevatecontrol` no MySQL. Depois ajuste as variáveis `DB_URL`, `DB_USER`, `DB_PASSWORD` para o processo Java (o arquivo `.env` sozinho **não é carregado automaticamente** pelo Maven). O Flyway realiza a criação do esquema na primeira inicialização.

## 3. Iniciar sem dados demo

Em ambiente local sem perfil demo, é possível criar o primeiro administrador pelas variáveis de ambiente:

```bash
export BOOTSTRAP_ADMIN_EMAIL='admin@meutcc.local'
export BOOTSTRAP_ADMIN_PASSWORD='EscolhaUmaSenhaForte123!'
export BOOTSTRAP_ADMIN_NAME='Administrador TCC'
mvn spring-boot:run
```

Isso só cria o administrador se a tabela de usuários estiver vazia. Depois o administrador cadastra clientes, prédios, elevadores, técnicos e usuários de cliente via API.

## 4. Compilar e executar testes

```bash
mvn clean test
mvn clean package
java -jar target/elevatecontrol-backend-1.0.0.jar
```

Se a compilação falhar, anote o primeiro erro do terminal; o restante pode ser consequência dele. Para executar `java -jar`, forneça as configurações de banco e, se necessário, perfil demo através das variáveis de ambiente.

## 5. Arquitetura e módulos

```text
src/main/java/com/elevatecontrol/backend/
├── ElevateControlApplication.java
├── api/          Controllers REST e contratos JSON (records)
├── domain/       Entidades JPA (Usuario, Cliente, Predio, Elevador, Chamado, Checklist...)
├── repository/   Consultas Spring Data JPA
├── service/      Regras de negócio, visita transacional e PDF
├── security/     Sessão, CSRF, permissões, bootstrap e dados demo
└── support/      Auditoria, conversores, catálogo 2026, erros e IDs

src/main/resources/
├── application.yml
├── application-prod.yml
└── db/migration/V1__schema.sql

docs/
├── CONTRATOS_API.md
└── GUIA-INTEGRACAO-REACT.md
```

As entidades representam as 6 coleções principais do React e as tabelas auxiliares para auditoria/recuperação de senha. As visitas armazenam o objeto `registroAtendimento` serializado em JSON dentro de `checklists`, para preservar o contrato profundamente aninhado do front-end. Em uma evolução, as respostas podem migrar para tabelas próprias, mantendo compatibilidade dos DTOs.

## 6. Fluxo operacional

1. Administrador cadastra cliente, prédio, elevador e técnico.
2. Cliente ou administrador abre um chamado. A descrição pode receber prioridade Crítica por **regra fixa** caso haja pessoas presas; a IA não decide emergências.
3. Administrador atribui técnico. Administrador ou técnico atribuído inicia o chamado.
4. Técnico (ou administrador) preenche visita, horários, até dois elevadores do mesmo prédio, tipo e situação, materiais, observações e assinaturas.
5. Se a visita for mensal, o sistema exige **29 códigos de resposta exclusivos** (S/N/NA) por elevador e assinatura do técnico e do responsável.
6. `ChecklistService.register()` salva a visita e atualiza chamado/elevadores em **uma única transação**. Só conclui o chamado quando todos os elevadores foram marcados exclusivamente como `Atendimento regularizado`. Caso contrário, fica `Em andamento` e aceita visitas posteriores.
7. Cada perfil acessa apenas informações autorizadas. O técnico pode consultar históricos dos prédios que já atendeu; o cliente, somente os próprios prédios.
8. Relatórios podem ser consultados como JSON ou baixados como PDF via `/api/relatorios/{id}/pdf`.

**Cuidados técnicos:** S/N/NA é resposta à pergunta específica, e não uma aprovação/reprovação automática. A sigla **LL** permanece como campo textual até a equipe definir seu significado. Assinatura desenhada não equivale, por si só, a identidade jurídica verificada; acrescentar critérios legais e trilha de auditoria se necessário.

## 7. Autenticação e CSRF

O React pretende usar **cookie de sessão HttpOnly**, não JWT no armazenamento local. O backend usa `JSESSIONID`, BCrypt e proteção CSRF (`XSRF-TOKEN` / `X-XSRF-TOKEN`). Antes de cada requisição de mutação:

```text
GET /api/auth/csrf
POST /api/auth/login
GET /api/auth/me
```

O `POST /api/auth/login` deve incluir `X-XSRF-TOKEN` recebido no GET e enviar os cookies via `credentials: 'include'`. O browser atual do front **não envia o token CSRF**; implemente a alteração em `src/services/http.ts` conforme `docs/GUIA-INTEGRACAO-REACT.md` antes de ativar `VITE_AUTH_MODE=api`.

## 8. Principais endpoints

| Área | Métodos / rotas |
|---|---|
| Login | `GET /api/auth/csrf`, `POST /api/auth/login`, `GET /api/auth/me`, `POST /api/auth/logout` |
| Usuários | `GET/POST /api/usuarios`, `GET /api/usuarios/{id}` |
| Clientes | `GET/POST /api/clientes`, `GET /api/clientes/{id}` |
| Prédios | `GET/POST /api/predios`, `GET /api/predios/{id}` |
| Elevadores | `GET/POST /api/elevadores`, `GET/PATCH /api/elevadores/{id}` |
| Chamados | `GET/POST /api/chamados`, `PATCH /api/chamados/{id}/atribuicao`, `POST /api/chamados/{id}/iniciar` |
| Visitas | `GET/POST /api/checklists`, `GET /api/checklists/{id}`, `GET /api/chamados/{id}/visitas` |
| Checklists | `GET /api/checklists/catalogo/mensal-2026` |
| Indicadores | `GET /api/dashboard/resumo` |
| Relatórios | `GET /api/relatorios`, `GET /api/relatorios/{id}/pdf` |
| Segurança | `POST /api/auth/forgot-password`, `POST /api/auth/reset-password`, `POST /api/auth/change-password` |
| Auditoria | `GET /api/auditoria` (admin) |

Confira o inventário completo de parâmetros, permissões e DTOs em **`docs/CONTRATOS_API.md`**.

## 9. Testar a API com curl ou Postman

Um roteiro básico encontra-se em `scripts/smoke.sh`. Teste as respostas por perfil, especialmente erro **403** quando um cliente tentar consultar outros prédios, técnico sem atribuição tentar registrar visita e checklist com apenas 28 rotinas.

## 10. Recuperação de senha

A API oferece recuperação com token temporário de 30 minutos, hash persistido e uso único. Ela **precisa de um SMTP configurado** para enviar os links. Quando não configurado, responde com HTTP 503, em vez de fingir que um e-mail foi enviado. A tela React de redefinição de senha ainda deverá ser criada.

## 11. Segurança e limitações antes de implantação real

Este backend fornece autenticação, escopo de dados, validações e auditoria de operações, mas ainda precisa de revisão de segurança/qualidade antes de lidar com clientes reais: TLS (HTTPS), `SESSION_COOKIE_SECURE=true`, segredos seguros, limitação de tentativas de login, backups, migrações por ambiente, critérios de validação de assinaturas, política de retenção de dados, paginação para bases grandes, observabilidade, autorização por empresa quando houver multitenancy, revisão legal/LGPD e testes integrados com MySQL. O Spring Boot 3.5.x já encerrou suporte OSS: atualize para uma versão suportada antes de produção.

## 12. O que **ainda não está integrado**

- As páginas React v0.5 continuam usando `AppContext` com dados locais. O ZIP do backend **não altera automaticamente o front-end**. Veja o guia em `docs/GUIA-INTEGRACAO-REACT.md`.
- Integração da IA com Spring AI/Ollama continua **planejada**, não implementada. A etapa virá depois que a API convencional estiver validada.
- Envio de alertas de emergência por SMS/WhatsApp/e-mail e operação de campo offline não foram implementados.

## Referência usada

Código do front-end *ElevateControl v0.5* e PDF *Mapeamento completo do backend Spring Boot v0.5*, produzidos para o TCC. O projeto tem fins educacionais e de desenvolvimento local.
