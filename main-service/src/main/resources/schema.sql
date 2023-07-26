CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories
(
    id   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(2000) NOT NULL,
    category           INTEGER REFERENCES categories (id) ON DELETE RESTRICT,
    created_on         TIMESTAMP WITHOUT TIME ZONE,
    description        VARCHAR,
    event_date         TIMESTAMP WITHOUT TIME ZONE,
    initiator          BIGINT REFERENCES users (id) ON DELETE CASCADE,
    lat                REAL          NOT NULL,
    lon                REAL          NOT NULL,
    paid               BOOLEAN       NOT NULL,
    participant_limit  INTEGER       NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN       NOT NULL,
    state              VARCHAR(50)   NOT NULL,
    title              VARCHAR(255)  NOT NULL
);

CREATE TABLE IF NOT EXISTS requests
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event     BIGINT REFERENCES events (id) ON DELETE CASCADE,
    requester BIGINT REFERENCES users (id) ON DELETE CASCADE,
    created   TIMESTAMP WITHOUT TIME ZONE,
    status    VARCHAR(50) NOT NULL,

    CONSTRAINT uniqueRequest UNIQUE (event, requester)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title  VARCHAR(255) NOT NULL UNIQUE,
    pinned BOOLEAN      NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_events
(
    compilation_id INTEGER REFERENCES compilations (id) ON DELETE CASCADE,
    event_id       BIGINT REFERENCES events (id) ON DELETE CASCADE,

    CONSTRAINT uniqueCompilationEvent UNIQUE (compilation_id, event_id)
);

DELETE
FROM requests;
DELETE
FROM events;
DELETE
FROM users;
DELETE
FROM categories;

ALTER TABLE users
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE categories
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE events
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE requests
    ALTER COLUMN id RESTART WITH 1;