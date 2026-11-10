CREATE TABLE password_reset_tokens (
 id BINARY(16) NOT NULL PRIMARY KEY,
 activo BOOLEAN NOT NULL DEFAULT TRUE,
 usuario_id BINARY(16) NOT NULL,
 token_hash CHAR(64) NOT NULL UNIQUE,
 expires_at TIMESTAMP(6) NOT NULL,
 used_at TIMESTAMP(6) NULL,
 CONSTRAINT fk_password_reset_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
CREATE INDEX ix_password_reset_usuario_usado ON password_reset_tokens(usuario_id, used_at);
