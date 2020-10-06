CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(32) NOT NULL PRIMARY KEY,
    password VARCHAR(64) NOT NULL,
    email VARCHAR(320) DEFAULT ''
);

CREATE TABLE IF NOT EXISTS notes (
    id BIGSERIAL PRIMARY KEY,
    author VARCHAR(32) NOT NULL REFERENCES users (username) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL DEFAULT 'Untitled',
    body TEXT DEFAULT '', --draft js content
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_viewed_by_author TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_viewed TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS shortcuts (
    username VARCHAR(32) REFERENCES users (username) ON DELETE CASCADE PRIMARY KEY,
    text_shortcuts TEXT DEFAULT '[]',
    style_shortcuts TEXT DEFAULT '[]',
    shared_style_shortcuts TEXT DEFAULT '[]', --todo key bindings will never be used, might want to remove
    generated_shortcuts TEXT DEFAULT '[]'
);

CREATE TABLE IF NOT EXISTS shares (
    id BIGSERIAL PRIMARY KEY,
    note_id BIGINT REFERENCES notes (id) ON DELETE CASCADE,
    shared_username VARCHAR(32) REFERENCES users (username) ON DELETE CASCADE
);

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS link_shares (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    author VARCHAR(32) REFERENCES users (username) ON DELETE CASCADE, --might not need because note_id has access to author
    note_id BIGINT REFERENCES notes (id) ON DELETE CASCADE,
    authority VARCHAR(18) DEFAULT 'VIEW',
    status VARCHAR(18) DEFAULT 'ACTIVE',
    times_used INT DEFAULT 0
);
