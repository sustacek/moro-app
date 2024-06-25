-- This file is executed by SpringBoot on startup, see application.properties -> spring.sql.init.mode
-- Use Postgres-specific SQL to have an sql script runnable even if the data already exists

-- NOTE: Make sure the manually inserted IDs in this file don't overlap with the sequence start
-- , see UserEntity (defaults to 50)
INSERT INTO users
    (id,    name,       username,   password_hash) VALUES
    (1,     'Petr',     'petr321',  'hash1' ) ON CONFLICT DO NOTHING;
INSERT INTO users
    (id,    name,       username,   password_hash) VALUES
    (2,     'Jana',     'janicka',  'hash2') ON CONFLICT DO NOTHING;
INSERT INTO users
    (id,    name,       username,   password_hash) VALUES
    (3,     'Olga',     'o111',     'hash3') ON CONFLICT DO NOTHING;
INSERT INTO users
    (id,    name,       username,   password_hash) VALUES
    (4,     'Martin',   'martas',   'hash4') ON CONFLICT DO NOTHING;