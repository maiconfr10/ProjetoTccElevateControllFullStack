# 🛗 ElevateControl — Gestão Inteligente de Elevadores

**Sistema Full Stack para gerenciamento de elevadores, chamados técnicos, manutenções e históricos de atendimento.**

Projeto acadêmico desenvolvido como **Trabalho de Conclusão de Curso (TCC)**, com o objetivo de centralizar a gestão de elevadores e melhorar a comunicação entre administradores, técnicos e clientes.

**Versão atual:** 0.7 — Em desenvolvimento.

## Tecnologias utilizadas

| Camada | Tecnologias |
|---|---|
| Frontend | React 19, TypeScript, Vite, Tailwind CSS |
| Backend | Java 21, Spring Boot, Spring Security, Spring Data JPA |
| Banco de dados | MySQL 8, Flyway |
| Relatórios | Apache PDFBox |
| Ferramentas | Git, GitHub, Maven, Postman, Spring Tools e VS Code |

## Funcionalidades

- Autenticação com três perfis: administrador, técnico e cliente.
- Criação automática de um administrador inicial.
- Troca obrigatória de senha no primeiro acesso.
- Cadastro de clientes, prédios, elevadores e usuários.
- Abertura e acompanhamento de chamados técnicos.
- Atribuição de chamados e técnicos responsáveis.
- Registro de visitas, manutenções e pendências.
- Checklist mensal de manutenção com 29 rotinas.
- Relatórios técnicos em PDF.
- Histórico de atendimentos e dashboards por perfil.
- Controle de permissões e acesso aos dados.

## Arquitetura do sistema

O projeto possui três camadas principais:

**Frontend:** interface desenvolvida em React e TypeScript, responsável pela interação com os usuários.

**Backend:** API REST desenvolvida em Java e Spring Boot, responsável pela autenticação, regras de negócio, validações e geração dos relatórios.

**Banco de dados:** MySQL utilizado para armazenar usuários, clientes, elevadores, chamados e históricos, com migrações gerenciadas pelo Flyway.

## Como executar

**1. Banco de dados**

Crie o banco `elevatecontrol` no MySQL. O Flyway criará as tabelas automaticamente ao iniciar o backend.

**2. Backend**

Configure `DB_URL`, `DB_USER`, `DB_PASSWORD` e as variáveis `BOOTSTRAP_ADMIN_*` no ambiente do Spring Boot.

Na primeira instalação, o sistema cria uma conta administrativa provisória que deverá ser personalizada pelo titular.

**3. Frontend**

Na pasta `elevatecontrol-frontend`, execute:

```bash
npm install
npm run dev
```

Acesse `http://localhost:5173`. O backend utiliza a porta `8090`.

**4. Dados demonstrativos**

Os scripts SQL estão em `elevatecontrol-backend/scripts/sql` e permitem preparar uma base de dados fictícia para apresentação.

O script de recriação exclui os registros existentes e deve ser executado somente quando a exclusão for intencional.

## Segurança

O sistema utiliza autenticação por sessão, permissões por perfil e armazenamento de senhas com BCrypt.

As credenciais reais do MySQL e do administrador devem ser configuradas por variáveis de ambiente e não devem ser publicadas no GitHub.

Os dados demonstrativos são fictícios e destinados exclusivamente ao ambiente de desenvolvimento e apresentação.

## Próximas melhorias

- Ampliação dos testes automatizados.
- Melhorias na experiência do usuário.
- Aprimoramento dos relatórios e indicadores.
- Possível integração com agentes de inteligência artificial para apoio à gestão.

*A integração de IA ainda é uma funcionalidade planejada.*

---

**ElevateControl — TCC | Desenvolvimento Full Stack | 2026**
