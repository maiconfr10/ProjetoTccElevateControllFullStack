-- ELEVATECONTROL v0.7 | DADOS EXCLUSIVAMENTE FICTICIOS | NAO EXECUTE EM PRODUCAO
-- Compatibilidade: MySQL 8.x e schema Flyway V1 + V2
-- Executar DEPOIS que Spring Boot iniciar e criar o admin inicial.
-- Contas de demonstracao usam senha CONHECIDA: Demo@2026Local!
-- O hash abaixo e BCrypt, nao texto puro. Exclusivamente ambiente local.
USE elevatecontrol;
SET NAMES utf8mb4;
START TRANSACTION;

INSERT INTO clientes (id, nome, documento, contato, email, telefone) VALUES
  ('CLI-01', 'Condomínio Aurora', '00.000.000/0001-01', 'Mariana Lopes', 'aurora@example.com', '(21) 90000-0001'),
  ('CLI-02', 'Grupo Horizonte', '00.000.000/0001-02', 'Pedro Silva', 'horizonte@example.com', '(21) 90000-0002'),
  ('CLI-03', 'Edifício Atlântico', '00.000.000/0001-03', 'Daniela Costa', 'atlantico@example.com', '(21) 90000-0003'),
  ('CLI-04', 'Residencial Jardim Norte', '00.000.000/0001-04', 'Carla Souza', 'jardim@example.com', '(21) 90000-0004');

INSERT INTO usuarios (id, nome, email, senha_hash, perfil, registro, cliente_id, ativo, troca_senha_obrigatoria, configuracao_inicial_pendente) VALUES
  ('USR-02', 'Rafael Oliveira', 'rafael.demo@example.com', '$2y$12$9ZJag2FDrzlj8erCv1a/mO3XWlHEwhah5X9btEOAudbopZOHsXGz.', 'tecnico', 'TEC-002', NULL, TRUE, FALSE, FALSE),
  ('USR-03', 'Bruno Mendes', 'bruno.demo@example.com', '$2y$12$9ZJag2FDrzlj8erCv1a/mO3XWlHEwhah5X9btEOAudbopZOHsXGz.', 'tecnico', 'TEC-003', NULL, TRUE, FALSE, FALSE),
  ('USR-06', 'Júlia Martins', 'julia.demo@example.com', '$2y$12$9ZJag2FDrzlj8erCv1a/mO3XWlHEwhah5X9btEOAudbopZOHsXGz.', 'tecnico', 'TEC-006', NULL, TRUE, FALSE, FALSE),
  ('USR-04', 'Mariana Lopes', 'mariana.demo@example.com', '$2y$12$9ZJag2FDrzlj8erCv1a/mO3XWlHEwhah5X9btEOAudbopZOHsXGz.', 'cliente', NULL, 'CLI-01', TRUE, FALSE, FALSE),
  ('USR-05', 'Pedro Silva', 'pedro.demo@example.com', '$2y$12$9ZJag2FDrzlj8erCv1a/mO3XWlHEwhah5X9btEOAudbopZOHsXGz.', 'cliente', NULL, 'CLI-02', TRUE, FALSE, FALSE),
  ('USR-07', 'Daniela Costa', 'daniela.demo@example.com', '$2y$12$9ZJag2FDrzlj8erCv1a/mO3XWlHEwhah5X9btEOAudbopZOHsXGz.', 'cliente', NULL, 'CLI-03', TRUE, FALSE, FALSE),
  ('USR-08', 'Carla Souza', 'carla.demo@example.com', '$2y$12$9ZJag2FDrzlj8erCv1a/mO3XWlHEwhah5X9btEOAudbopZOHsXGz.', 'cliente', NULL, 'CLI-04', TRUE, FALSE, FALSE);

INSERT INTO predios (id, cliente_id, nome, endereco) VALUES
  ('PRD-01', 'CLI-01', 'Residencial Aurora', 'Rio de Janeiro - RJ'),
  ('PRD-04', 'CLI-01', 'Bloco Aurora II', 'Rio de Janeiro - RJ'),
  ('PRD-02', 'CLI-02', 'Torre Horizonte', 'Rio de Janeiro - RJ'),
  ('PRD-05', 'CLI-02', 'Edifício Jardim Sul', 'Rio de Janeiro - RJ'),
  ('PRD-03', 'CLI-03', 'Edifício Atlântico', 'Rio de Janeiro - RJ'),
  ('PRD-06', 'CLI-04', 'Residencial Jardim Norte', 'Rio de Janeiro - RJ');

INSERT INTO elevadores (id, predio_id, identificacao, marca, modelo, status, pendencia) VALUES
  ('ELV-01', 'PRD-01', 'Social 01', 'Atlas', 'LX-400', 'Em manutenção', 'Troca de óleo pendente; aguarda material adequado.'),
  ('ELV-02', 'PRD-01', 'Serviço 02', 'Schindler', 'S-200', 'Em manutenção', 'Ruído a investigar durante o deslocamento.'),
  ('ELV-03', 'PRD-04', 'Social 01', 'Atlas', 'LX-400', 'Operacional', ''),
  ('ELV-04', 'PRD-02', 'Principal 01', 'Otis', 'Gen2', 'Operacional', ''),
  ('ELV-05', 'PRD-02', 'Serviço 01', 'Schindler', '3300', 'Em manutenção', 'Oscilação de nivelamento sob análise.'),
  ('ELV-06', 'PRD-03', 'Torre A', 'Thyssenkrupp', 'Evolution', 'Fora de serviço', 'Chamado de emergência demonstrativo em investigação.'),
  ('ELV-07', 'PRD-03', 'Serviço B', 'Atlas', 'LX-500', 'Operacional', ''),
  ('ELV-08', 'PRD-05', 'Principal 01', 'Otis', 'Gen2', 'Operacional', ''),
  ('ELV-09', 'PRD-06', 'Social 01', 'Atlas', 'LX-300', 'Operacional', ''),
  ('ELV-10', 'PRD-06', 'Serviço 01', 'Schindler', '3100', 'Fora de serviço', 'Falha simulada no dispositivo de alarme.');

COMMIT;
