-- EXECUTAR APOS A CARGA PARA CONFERIR OS VOLUMES E O VINCULO DOS DADOS.
USE elevatecontrol;
SELECT 'administradores' AS categoria, COUNT(*) AS total FROM usuarios WHERE perfil='admin'
UNION ALL SELECT 'tecnicos_demo', COUNT(*) FROM usuarios WHERE perfil='tecnico'
UNION ALL SELECT 'clientes_demo_usuarios', COUNT(*) FROM usuarios WHERE perfil='cliente'
UNION ALL SELECT 'clientes', COUNT(*) FROM clientes
UNION ALL SELECT 'predios', COUNT(*) FROM predios
UNION ALL SELECT 'elevadores', COUNT(*) FROM elevadores
UNION ALL SELECT 'chamados', COUNT(*) FROM chamados
UNION ALL SELECT 'checklists', COUNT(*) FROM checklists;

SELECT id, nome, email, perfil, troca_senha_obrigatoria, configuracao_inicial_pendente
FROM usuarios WHERE perfil = 'admin';

SELECT c.id, c.titulo, c.tipo, c.prioridade, c.status,
       p.nome AS predio, e.identificacao AS elevador,
       u.nome AS tecnico
FROM chamados c
JOIN predios p ON p.id = c.predio_id
JOIN elevadores e ON e.id = c.elevador_id
LEFT JOIN usuarios u ON u.id = c.tecnico_id
ORDER BY c.data DESC;

SELECT ch.id, ch.tipo, ch.chamado_id, ch.responsavel,
       CASE WHEN ch.assinatura IS NULL OR ch.assinatura = ''
         THEN 'Sem assinatura de aprovacao' ELSE 'Assinatura DEMO ilustrativa' END AS validacao
FROM checklists ch ORDER BY ch.concluido_em DESC;
