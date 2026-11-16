-- ELEVATECONTROL v0.7 | DADOS EXCLUSIVAMENTE FICTICIOS | NAO EXECUTE EM PRODUCAO
-- Compatibilidade: MySQL 8.x e schema Flyway V1 + V2
-- Requer usuarios de teste e o administrador criado pelo Spring Boot.
USE elevatecontrol;
SET NAMES utf8mb4;
SET @admin_id = (SELECT id FROM usuarios WHERE perfil = 'admin' AND ativo = TRUE ORDER BY id LIMIT 1);
-- Se @admin_id for NULL, PARE: suba o Spring com BOOTSTRAP_ADMIN_* primeiro.
SELECT @admin_id AS administrador_criado_pelo_spring;
START TRANSACTION;

INSERT INTO chamados (id, titulo, descricao, predio_id, elevador_id, tipo, prioridade, status, tecnico_id, criado_por, data, concluido_em, versao) VALUES
  ('CH-1001', 'Ruído durante a subida', 'Acompanhamento fictício de ruído no elevador de serviço; vistoria pendente.', 'PRD-01', 'ELV-02', 'Corretiva', 'Alta', 'Em andamento', 'USR-02', 'USR-04', '2026-11-13 08:50:00', NULL, 0),
  ('CH-1002', 'Manutenção preventiva mensal', 'Agendamento fictício de rotina de manutenção preventiva.', 'PRD-02', 'ELV-04', 'Preventiva', 'Normal', 'Aberto', 'USR-03', @admin_id, '2026-11-14 10:00:00', NULL, 0),
  ('CH-1003', 'Elevador parou entre andares', 'Emergência simulada: equipamento retirado de serviço. Ninguém ficou preso.', 'PRD-03', 'ELV-06', 'Emergência', 'Crítica', 'Em andamento', 'USR-06', @admin_id, '2026-11-14 15:15:00', NULL, 0),
  ('CH-1004', 'Porta com fechamento irregular', 'Solicitação demonstrativa do condomínio; sem vistoria física.', 'PRD-01', 'ELV-01', 'Corretiva', 'Alta', 'Aberto', NULL, 'USR-04', '2026-11-15 08:30:00', NULL, 0),
  ('CH-1005', 'Troca de óleo', 'Visita de teste para avaliar necessidade de material; serviço não executado.', 'PRD-01', 'ELV-01', 'Corretiva', 'Normal', 'Em andamento', 'USR-02', @admin_id, '2026-11-15 09:20:00', NULL, 0),
  ('CH-1006', 'Inspeção preventiva programada', 'Chamado fictício de inspeção do elevador social.', 'PRD-04', 'ELV-03', 'Preventiva', 'Normal', 'Aberto', 'USR-03', @admin_id, '2026-11-15 11:10:00', NULL, 0),
  ('CH-1007', 'Oscilação no nivelamento', 'Registro fictício de oscilação; solicita-se apoio técnico.', 'PRD-02', 'ELV-05', 'Corretiva', 'Alta', 'Em andamento', 'USR-03', 'USR-05', '2026-11-14 09:10:00', NULL, 0),
  ('CH-1008', 'Iluminação da cabine', 'Serviço demonstrativo concluído com validação fictícia identificada no relatório.', 'PRD-03', 'ELV-07', 'Corretiva', 'Normal', 'Concluído', 'USR-06', 'USR-07', '2026-11-09 10:00:00', '2026-11-10 16:30:00', 0),
  ('CH-1009', 'Checklist mensal do elevador social', 'Checklist 2026 fictício: marcações NA, sem inspeção física.', 'PRD-01', 'ELV-01', 'Preventiva', 'Normal', 'Em andamento', 'USR-02', @admin_id, '2026-11-15 15:40:00', NULL, 0),
  ('CH-1010', 'Alarme de emergência intermitente', 'Registro de demonstração de equipamento parado; sem ocorrência real.', 'PRD-06', 'ELV-10', 'Emergência', 'Crítica', 'Aberto', NULL, 'USR-08', '2026-11-16 08:15:00', NULL, 0),
  ('CH-1011', 'Vistoria de rotina', 'Solicitação fictícia para apresentar agendamento e atribuição.', 'PRD-05', 'ELV-08', 'Preventiva', 'Normal', 'Aberto', 'USR-03', 'USR-05', '2026-11-16 09:00:00', NULL, 0);

COMMIT;
