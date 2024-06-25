-- This file is executed by SpringBoot on startup, see application.properties -> spring.sql.init.mode
-- Use Postgres-specific SQL to have an sql script runnable even if the data already exists

-- NOTE: Make sure the manually inserted IDs in this file don't overlap with the sequence start
-- , see UserEntity (defaults to 50)

-- The password before hashing was 'b20942a6-6fd1-42dc-bce9-7697fd03fac0'
INSERT INTO users
    (id,    name,       username,   password_hash) VALUES
    (1,     'Petr',     'petr321',  '{bcrypt}$2a$10$VUPRfWvb2hHfXK6va04u9OMcKCxRJAluASNWRq.HDo800yaEXVVla' ) ON CONFLICT DO NOTHING;

-- The password before hashing was '051b24f7-e053-4f64-b805-7b5734b72fc1'
INSERT INTO users
    (id,    name,       username,   password_hash) VALUES
    (2,     'Jana',     'janca',  '{bcrypt}$2a$10$F/lToBJJhVlG18h5CEBJGeBRTinoKqCdOUSu/.1SgyHkoXepF3h8.') ON CONFLICT DO NOTHING;

-- The password before hashing was '8dd0cb3e-b59d-4917-aa26-e2fb0e05ba45'
INSERT INTO users
    (id,    name,       username,    password_hash) VALUES
    (3,     'Olga',     'olinka111', '{bcrypt}$2a$10$VEnsrkEV.ax2JvFMCncSv.iDucK9QnCcLpbOV5zpvyjx8K69tgTQ6') ON CONFLICT DO NOTHING;

-- The password before hashing was '23b4f81a-9f2f-4323-ac98-68fa90fd091d'
INSERT INTO users
    (id,    name,       username,   password_hash) VALUES
    (4,     'Martin',   'martas',   '{bcrypt}$2a$10$kv8odOqcpsPGKnQSdyd1F.4XbprwbXLdXp.hew6rAnwROD423XQrS') ON CONFLICT DO NOTHING;