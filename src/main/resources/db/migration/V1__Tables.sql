CREATE TABLE users (
    username VARCHAR(32) NOT NULL PRIMARY KEY,
    password VARCHAR(64) NOT NULL,
    email VARCHAR(320) DEFAULT ''
);

CREATE TABLE notes (
    id BIGSERIAL PRIMARY KEY,
    author VARCHAR(32) NOT NULL,
    title VARCHAR(200) NOT NULL DEFAULT 'Untitled',
    body TEXT DEFAULT '' --draft js content
);

CREATE TABLE shortcuts (
    username VARCHAR(32) REFERENCES users (username) ON DELETE CASCADE PRIMARY KEY,
    text_shortcuts TEXT DEFAULT '[]',
    style_shortcuts TEXT DEFAULT '[]'
);
