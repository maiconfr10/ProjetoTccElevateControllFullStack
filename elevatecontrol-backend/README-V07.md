# ElevateControl v0.7 — Backend Spring Boot

## O que mudou

- Migração **V2__primeiro_acesso.sql** adiciona `troca_senha_obrigatoria` e `configuracao_inicial_pendente` à tabela `usuarios`, sem apagar contas existentes.
- Usuários criados pelo administrador recebem senha temporária e **obrigação de troca no primeiro login**.
- O administrador de instalação pode ser criado automaticamente **quando não existir administrador** e as credenciais iniciais forem configuradas por variáveis de ambiente. Ele deverá configurar nome, e-mail e senha antes de acessar as funcionalidades.
- A API impede o acesso operacional de contas com configuração pendente, mesmo que tentem chamar endpoints diretamente.
- Usuários autenticados podem atualizar dados do próprio perfil (com senha atual) e trocar a senha.
- Visitas com pendências podem ser registradas **sem assinatura de aprovação do cliente**. A conclusão de serviços regularizados exige nome e assinatura; autorização de fornecimento, quando declarada, exige assinatura específica. A assinatura do técnico permanece necessária no checklist mensal.
- Saída da visita deve ser **posterior** à chegada.
- Técnico pode consultar nomes de clientes de seus prédios autorizados, sem acesso aos seus dados de contato.
- Respostas de chamados para o perfil `cliente` não incluem a atribuição interna do técnico.
- Relatório PDF com cabeçalho corporativo, rodapé, datas brasileiras, identificação textual do técnico/equipamento e indicação de validação pendente.

## Instalação segura no banco atual

1. Faça backup do banco `elevatecontrol` no MySQL Workbench (Server > Data Export).
2. Substitua apenas os **arquivos-fonte** do projeto backend. Preserve sua configuração local de ambiente e o banco existente; **não execute scripts de exclusão/recriação**.
3. No Spring Tools, faça **Maven > Update Project** e depois execute **Run As > Spring Boot App**.
4. O Flyway executa a migração `V2__primeiro_acesso.sql` uma única vez. Não edite `V1__schema.sql`.
5. Verifique `http://localhost:8090/actuator/health` (`UP`).

## Administrador inicial — instalações novas

Antes de subir o backend em **um banco sem administrador**, configure no ambiente do processo Java:

```text
BOOTSTRAP_ADMIN_EMAIL=<email-de-instalacao>
BOOTSTRAP_ADMIN_PASSWORD=<senha-temporaria-exclusiva-com-12-ou-mais-caracteres>
BOOTSTRAP_ADMIN_NAME=Administrador Inicial
```

No Spring Tools: **Run > Run Configurations > Spring Boot App > Environment > New** para inserir essas variáveis. Não use uma senha pública fixa, não inclua essas credenciais no Git e não deixe valores reais em arquivos versionados. Um arquivo `.env` isolado não é lido automaticamente pelo Spring Boot.

Se o banco já contiver Camila (`admin`) ou outro administrador, **nenhuma conta adicional será criada**. Em instalações novas sem administrador e sem variáveis de bootstrap, a inicialização falhará com instrução explícita.

Após entrar com a conta provisória, a interface solicita **nome, e-mail e nova senha**. Os dados são preservados no banco, e a conta não é recriada quando o servidor reinicia.

**Importante:** usuários já cadastrados antes da V2 recebem as novas flags como `FALSE` para não bloquear contas atuais. Para exigir a troca de senha de uma conta existente de teste, faça isso apenas com autorização, por exemplo:

```sql
UPDATE usuarios SET troca_senha_obrigatoria = TRUE
WHERE email = 'matheus@gmail.com';
```

## Endpoints novos

| Método | Rota | Uso |
|---|---|---|
| GET | `/api/auth/me` | Retorna também `trocaSenhaObrigatoria` e `configuracaoInicialPendente` |
| POST | `/api/auth/change-password` | `{ "senhaAtual": "...", "novaSenha": "..." }` |
| POST | `/api/auth/complete-setup` | `{ "nome":"...", "email":"...", "senhaTemporaria":"...", "novaSenha":"..." }` |
| PUT | `/api/auth/profile` | `{ "nome":"...", "email":"...", "senhaAtual":"..." }` |
| GET | `/api/clientes/nomes-visiveis` | Lista apenas `{id,nome}` de clientes relacionados aos prédios autorizados |

Todos os endpoints de alteração usam a sessão autenticada e proteção CSRF. Não desative a segurança para facilitar testes.

## Testes recomendados

1. Verifique o banco existente e seu login atual.
2. Cadastre um técnico de teste novo; confirme que o acesso às outras APIs retorna `403` antes da troca de senha; após trocar, confirme `200` nos endpoints permitidos.
3. Cadastre uma visita com `Pendência de Material`, sem assinatura de aprovação; confirme visita registrada e chamado `Em andamento`.
4. Tente concluir serviço regularizado sem assinatura; confirme rejeição (HTTP 400).
5. Gere o PDF e confira visualmente páginas e assinatura apenas quando efetivamente fornecida.
6. Login de cliente: não exibir `tecnicoId` nos detalhes do chamado nem permitir acesso a dados de outro condomínio.

Execute no terminal do projeto:

```bash
mvn test
mvn spring-boot:run
```

**Validação desta entrega:** 55 arquivos Java passaram na análise de sintaxe. O build Maven e o teste com MySQL precisam ser executados no computador com as dependências Spring Boot instaladas; não foram concluídos aqui.


## Instalação limpa para apresentação TCC

Consulte [README-DEMONSTRACAO.md](README-DEMONSTRACAO.md) e os scripts em `scripts/sql/`. O seeder Java legado foi removido em favor dos SQL manuais; nunca versione senhas do primeiro administrador.
