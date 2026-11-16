-- Migração aditiva: usuários existentes mantêm acesso. Nenhum registro é apagado.
ALTER TABLE usuarios ADD COLUMN troca_senha_obrigatoria BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE usuarios ADD COLUMN configuracao_inicial_pendente BOOLEAN NOT NULL DEFAULT FALSE;
