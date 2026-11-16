-- ================================================================
-- PERIGO: APAGA TODO O CONTEUDO DO BANCO elevatecontrol.
-- Execute SOMENTE no MySQL local de testes, apos PARAR o Spring Boot.
-- Execute como usuario MySQL autorizado a DROP/CREATE DATABASE.
-- BACKUP: MySQL Workbench > Server > Data Export, se desejar preservar.
-- ================================================================
DROP DATABASE IF EXISTS elevatecontrol;
CREATE DATABASE elevatecontrol
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Se a conta 'elevate'@'localhost' ja foi criada, mas nao possuir
-- permissoes no banco novo, execute como administrador MySQL:
-- GRANT ALL PRIVILEGES ON elevatecontrol.* TO 'elevate'@'localhost';
-- Nao inclua senhas de banco em scripts publicos.
