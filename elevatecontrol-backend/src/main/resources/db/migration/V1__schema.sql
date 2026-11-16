CREATE TABLE clientes (
 id VARCHAR(64) PRIMARY KEY, nome VARCHAR(160) NOT NULL, documento VARCHAR(40) NOT NULL UNIQUE,
 contato VARCHAR(140) NOT NULL, email VARCHAR(180) NOT NULL, telefone VARCHAR(40) NOT NULL
);
CREATE TABLE usuarios (
 id VARCHAR(64) PRIMARY KEY, nome VARCHAR(120) NOT NULL, email VARCHAR(180) NOT NULL UNIQUE,
 senha_hash VARCHAR(100) NOT NULL, perfil VARCHAR(20) NOT NULL, registro VARCHAR(120), cliente_id VARCHAR(64),
 ativo BOOLEAN NOT NULL DEFAULT TRUE,
 CONSTRAINT fk_usuario_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);
CREATE TABLE predios (
 id VARCHAR(64) PRIMARY KEY, cliente_id VARCHAR(64) NOT NULL, nome VARCHAR(160) NOT NULL, endereco VARCHAR(300) NOT NULL,
 CONSTRAINT fk_predio_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);
CREATE TABLE elevadores (
 id VARCHAR(64) PRIMARY KEY, predio_id VARCHAR(64) NOT NULL, identificacao VARCHAR(120) NOT NULL,
 marca VARCHAR(120) NOT NULL, modelo VARCHAR(120) NOT NULL, status VARCHAR(40) NOT NULL, pendencia VARCHAR(2000),
 CONSTRAINT fk_elevador_predio FOREIGN KEY (predio_id) REFERENCES predios(id)
);
CREATE TABLE chamados (
 id VARCHAR(64) PRIMARY KEY, titulo VARCHAR(100) NOT NULL, descricao LONGTEXT,
 predio_id VARCHAR(64) NOT NULL, elevador_id VARCHAR(64) NOT NULL,
 tipo VARCHAR(24) NOT NULL, prioridade VARCHAR(24) NOT NULL, status VARCHAR(40) NOT NULL,
 tecnico_id VARCHAR(64), criado_por VARCHAR(64) NOT NULL, data DATETIME(6) NOT NULL,
 concluido_em DATETIME(6), versao BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT fk_chamado_predio FOREIGN KEY (predio_id) REFERENCES predios(id),
 CONSTRAINT fk_chamado_elevador FOREIGN KEY (elevador_id) REFERENCES elevadores(id),
 CONSTRAINT fk_chamado_tecnico FOREIGN KEY (tecnico_id) REFERENCES usuarios(id),
 CONSTRAINT fk_chamado_criador FOREIGN KEY (criado_por) REFERENCES usuarios(id)
);
CREATE TABLE checklists (
 id VARCHAR(64) PRIMARY KEY, chamado_id VARCHAR(64) NOT NULL, tecnico_id VARCHAR(64),
 tipo VARCHAR(24) NOT NULL, inicio DATETIME(6) NOT NULL, fim DATETIME(6) NOT NULL,
 lubrificacao BOOLEAN NOT NULL, teste_seguranca BOOLEAN NOT NULL, pecas VARCHAR(1000),
 observacoes LONGTEXT, responsavel VARCHAR(120) NOT NULL, assinatura LONGTEXT,
 concluido_em DATETIME(6) NOT NULL, registro_atendimento_json LONGTEXT,
 CONSTRAINT fk_checklist_chamado FOREIGN KEY (chamado_id) REFERENCES chamados(id),
 CONSTRAINT fk_checklist_tecnico FOREIGN KEY (tecnico_id) REFERENCES usuarios(id)
);
CREATE TABLE tokens_recuperacao (
 id VARCHAR(64) PRIMARY KEY, usuario_id VARCHAR(64) NOT NULL, token_hash VARCHAR(64) NOT NULL UNIQUE,
 expira_em DATETIME(6) NOT NULL, usado_em DATETIME(6),
 CONSTRAINT fk_token_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
CREATE TABLE auditoria (
 id VARCHAR(64) PRIMARY KEY, usuario_id VARCHAR(64), acao VARCHAR(80) NOT NULL,
 recurso VARCHAR(80) NOT NULL, recurso_id VARCHAR(64), quando DATETIME(6) NOT NULL,
 CONSTRAINT fk_audit_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
CREATE INDEX ix_predio_cliente ON predios(cliente_id);
CREATE INDEX ix_elevador_predio ON elevadores(predio_id);
CREATE INDEX ix_chamado_predio ON chamados(predio_id);
CREATE INDEX ix_chamado_tecnico ON chamados(tecnico_id);
CREATE INDEX ix_chamado_status ON chamados(status);
CREATE INDEX ix_checklist_chamado ON checklists(chamado_id);
CREATE INDEX ix_checklist_tecnico ON checklists(tecnico_id);
