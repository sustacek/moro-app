-- This file is executed by SpringBoot on startup, see application.properties -> spring.sql.init.mode
-- Use Postgres-specific SQL to have an sql script runnable even if the data already exists

INSERT INTO users (id, name) VALUES (1, 'Petr') ON CONFLICT DO NOTHING;
INSERT INTO users (id, name) VALUES (2, 'Jana') ON CONFLICT DO NOTHING;
INSERT INTO users (id, name) VALUES (3, 'Olga') ON CONFLICT DO NOTHING;
INSERT INTO users (id, name) VALUES (4, 'Martin') ON CONFLICT DO NOTHING;