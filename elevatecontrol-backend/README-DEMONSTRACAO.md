# ElevateControl v0.7 — Instalação limpa e dados para apresentação (TCC)

> **Atenção:** o script `00_RECRIAR_BANCO_APAGA_TUDO.sql` faz `DROP DATABASE elevatecontrol` e destrói os dados existentes. Use apenas no ambiente local fictício, com backup se precisar. Não execute estes scripts em produção.

## O que já existe no backend

- O **Flyway** cria as tabelas automaticamente (`V1__schema.sql` e `V2__primeiro_acesso.sql`). **Não execute V1/V2 manualmente no Workbench.**
- Quando a aplicação sobe com um banco novo e as variáveis `BOOTSTRAP_ADMIN_*`, o Spring Boot cria **um administrador provisório**, com senha BCrypt, `troca_senha_obrigatoria=true` e `configuracao_inicial_pendente=true`.
- O proprietário faz login e conclui a configuração inicial no React: **nome, e-mail e nova senha**. No próximo restart, o Spring **não recria nem altera** o administrador.
- Os cadastros e registros demonstrativos são SQL **opcional, manual e separado das migrações**, para não popular um ambiente de produção por acidente.

## Ordem de execução: não inverter

1. **Pare o Spring Boot** no STS/Eclipse (botão vermelho Stop). Pare também eventuais containers apontando para o mesmo banco.
2. No MySQL Workbench, conecte-se com um usuário autorizado a apagar/criar bancos e execute `scripts/sql/00_RECRIAR_BANCO_APAGA_TUDO.sql`. Isto **apaga todos os dados atuais**, inclusive Camila e os chamados de teste.
3. Se aparecer `Access denied` depois da recriação, execute como administrador MySQL: `GRANT ALL PRIVILEGES ON elevatecontrol.* TO 'elevate'@'localhost';` (apenas se essa conta já existir). Se usar outro usuário MySQL, ajuste `DB_USER`.
4. No STS/Eclipse, abra **Run > Run Configurations... > Spring Boot App > ElevateControlApplication > Environment**. Configure as variáveis abaixo **sem versionar valores secretos**:
   - `BOOTSTRAP_ADMIN_EMAIL=instalacao@example.com`
   - `BOOTSTRAP_ADMIN_NAME=Administrador Inicial`
   - `BOOTSTRAP_ADMIN_PASSWORD=<crie você uma senha temporária exclusiva com 12 ou mais caracteres>`
   - `DB_URL=jdbc:mysql://localhost:3306/elevatecontrol?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC`
   - `DB_USER=elevate` (se for esta a conta local)
   - `DB_PASSWORD=<senha local do usuário MySQL>`
5. Inicie **Run As > Spring Boot App**. O Flyway cria o esquema; depois, o Spring cria o administrador. Verifique `http://localhost:8090/actuator/health` -> `{"status":"UP"}`.
6. Acesse o React `http://localhost:5173`, faça login com o e-mail e senha de instalação e **configure os dados do titular**. Caso altere o e-mail, use o novo no próximo login.
7. **Após criar o administrador**, execute os scripts SQL no Workbench exatamente nesta ordem:
   - `01_CADASTROS_DEMO.sql`
   - `02_CHAMADOS_DEMO.sql`
   - `03_VISITAS_RELATORIOS_DEMO.sql`
   - `04_CONFERIR_DADOS.sql` (só consulta)
8. Atualize o React com `Ctrl+F5`. Teste os três perfis e baixe os PDFs dos registros.

**Importante:** não ative o seeder Java legado (`DemoSeeder`); ele foi retirado desta versão para garantir que **somente** o Spring Bootstrap crie a conta administradora e que os dados demonstrativos sejam controlados pelos scripts SQL.

## Volumes de demonstração esperados

| Recurso | Quantidade |
|---|---:|
| Administrador inicial | 1, criado pelo Spring Boot |
| Técnicos demonstrativos | 3 |
| Clientes com login | 4 |
| Clientes/condomínios | 4 |
| Prédios | 6 |
| Elevadores | 10 |
| Chamados | 11 |
| Registros de visita/relatórios | 4 |

Os históricos incluem pendência de material, solicitação de apoio técnico, um exemplo **inteiramente fictício** de serviço regularizado e um checklist mensal 2026 com 29 itens **NA**. Nenhuma inspeção real é atestada por esses dados.

## Contas de DEMONSTRAÇÃO (não usar em produção)

**Apenas para teste local**, os 7 usuários demonstrativos são adicionados pelo SQL com uma senha conhecida e hash BCrypt.

- Senha dos usuários de demonstração: `Demo@2026Local!`
- Técnicos: `rafael.demo@example.com`, `bruno.demo@example.com`, `julia.demo@example.com`
- Clientes: `mariana.demo@example.com` (Aurora), `pedro.demo@example.com` (Horizonte), `daniela.demo@example.com` (Atlântico), `carla.demo@example.com` (Jardim Norte)
- **Administrador:** não há senha no SQL; as credenciais da conta provisória vêm exclusivamente das variáveis privadas do Spring.

**Segurança:** como as credenciais demo são públicas neste repositório, jamais execute os scripts em servidor acessível externamente. Para demonstrar uma instalação real, remova as contas demo e cadastre usuários pela interface com senha temporária exclusiva e troca obrigatória.

## Organização para o GitHub

```text
elevatecontrol-backend/
├── src/main/resources/db/migration/
│   ├── V1__schema.sql                 # Flyway automático
│   └── V2__primeiro_acesso.sql        # Flyway automático
├── src/main/java/.../security/
│   └── BootstrapAdmin.java            # Criação automática do primeiro admin
├── scripts/sql/
│   ├── 00_RECRIAR_BANCO_APAGA_TUDO.sql
│   ├── 01_CADASTROS_DEMO.sql
│   ├── 02_CHAMADOS_DEMO.sql
│   ├── 03_VISITAS_RELATORIOS_DEMO.sql
│   └── 04_CONFERIR_DADOS.sql
├── .env.example                       # Apenas variáveis de exemplo, sem segredo
├── .gitignore                         # Exclui .env e artefatos de build
└── README-DEMONSTRACAO.md
```

Suba para o GitHub os arquivos `.sql`, o código Java, a documentação e `.env.example`. **Não suba:** `.env` real, dumps com dados reais, senhas do banco, credenciais de bootstrap, pastas `target/` ou `node_modules/`.

## Diagnósticos frequentes

- **Falha na inicialização por ausência de admin:** defina `BOOTSTRAP_ADMIN_EMAIL` e `BOOTSTRAP_ADMIN_PASSWORD` no ambiente do processo Java (um arquivo `.env` não é carregado automaticamente pelo Spring).
- **Tabelas não existem:** confirme Flyway e banco correto em `DB_URL`; não rode seed antes de iniciar o backend.
- **Usuário admin inicial não apareceu:** verifique se o banco realmente foi recriado ou se já existe `perfil='admin'`.
- **Conta demo não consegue login:** confira se rodou `01_CADASTROS_DEMO.sql` e se o front-end aponta para a API na porta 8090.
- **Chave estrangeira no SQL:** confirme a ordem dos arquivos. Não execute duas vezes sobre os mesmos dados; o seed é para um banco recém-criado.

## Validação

Após popular, rode `04_CONFERIR_DADOS.sql`. No Spring Tools, rode `mvn test` ou **Run As > Maven test** e valide as rotas com login. A compilação e a execução real no seu MySQL local precisam ser testadas no seu computador; este pacote não se conectou ao seu banco.
