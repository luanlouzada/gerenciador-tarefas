-- Criar extensões
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Função para gerar UUIDv7
CREATE OR REPLACE FUNCTION uuid_generate_v7()
RETURNS UUID AS $$
DECLARE
ts_ms BIGINT;
    ts_hex TEXT;
    random_hex TEXT;
    uuid_str TEXT;
BEGIN
    ts_ms := FLOOR(EXTRACT(EPOCH FROM CLOCK_TIMESTAMP()) * 1000)::BIGINT;
    ts_hex := lpad(to_hex(ts_ms), 12, '0');
    random_hex := encode(gen_random_bytes(10), 'hex');
    uuid_str := ts_hex || random_hex;
    uuid_str := substring(uuid_str from 1 for 8) || '-' ||
                substring(uuid_str from 9 for 4) || '-' ||
                substring(uuid_str from 13 for 4) || '-' ||
                substring(uuid_str from 17 for 4) || '-' ||
                substring(uuid_str from 21 for 12);
    uuid_str := overlay(uuid_str placing '7' from 15 for 1);
    uuid_str := overlay(uuid_str placing '8' from 20 for 1);
RETURN uuid_str::UUID;
EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'Erro ao gerar UUIDv7: %', SQLERRM;
END;
$$ LANGUAGE plpgsql;


CREATE TABLE IF NOT EXISTS "users" (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v7(),
                       name VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE
);

CREATE INDEX idx_users_email ON users(email);