# ElevateControl v0.7 — Guia de atualização do TCC

**Projetos entregues:** React + TypeScript (front-end) e Spring Boot 3 / Java 21 (backend). Banco de dados: MySQL (`elevatecontrol`).

## Antes de atualizar

- Exporte um backup do MySQL.
- Mantenha sua pasta `.git` e as configurações de conexão já usadas no computador.
- Não substitua o banco de dados nem reexecute scripts de criação manual.
- Atualize primeiro o **backend**, depois o **front-end**. A versão 0.7 do front depende das novas rotas da API.

## Backend

1. Extraia o ZIP backend sobre uma cópia do projeto existente.
2. Spring Tools > Import Existing Maven Projects, se necessário.
3. Maven > Update Project.
4. Verifique usuário/senha de MySQL em suas variáveis de ambiente ou no `application.yml`.
5. Execute como Spring Boot App. O Flyway instalará apenas a migração V2.
6. Acesse `http://localhost:8090/actuator/health`.

**Primeira instalação sem administrador:** forneça `BOOTSTRAP_ADMIN_EMAIL`, `BOOTSTRAP_ADMIN_PASSWORD` e `BOOTSTRAP_ADMIN_NAME` nas variáveis de ambiente do Spring Tools. O sistema cria a conta automaticamente apenas se não houver administrador. O titular deverá atualizar os dados e a senha no primeiro acesso. Senhas reais nunca devem ir ao Git.

## Front-end

1. Extraia o ZIP front-end em uma pasta.
2. Confirme que `.env` tem `VITE_AUTH_MODE=api` e `VITE_API_URL=http://localhost:8090/api`.
3. Execute `npm install`, `npm run typecheck` e `npm run dev`.
4. Abra `http://localhost:5173` e faça os testes descritos no README de cada projeto.

## Melhorias implementadas

- Primeiro acesso e troca de senha obrigatória para novos usuários.
- Administrador inicial configurável pelo titular, sem senhas públicas codificadas.
- Edição de nome, e-mail e senha no próprio perfil.
- Visitas com pendências sem falsa assinatura do cliente, com conclusão protegida.
- Avisos de sucesso amigáveis e ocultação de atribuição interna para clientes.
- Nome de cliente preenchido por endpoint de acesso limitado e horários validados.
- Gerador de relatórios PDF com cabeçalho e rodapé corporativos, nomes e datas formatadas.

## Verificações nesta entrega

- **TypeScript:** passou (`tsc -b`).
- **Java:** 55 arquivos analisados, nenhum erro de sintaxe.
- **A validar no Spring Tools:** `mvn test`, compilação das dependências, inicialização real, Flyway V2, login/cookies/CSRF e emissão do PDF com os dados do MySQL. Não foram executados remotamente.
