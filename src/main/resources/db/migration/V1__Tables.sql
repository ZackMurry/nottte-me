CREATE TABLE users (
    username VARCHAR(32) NOT NULL PRIMARY KEY,
    password VARCHAR(64) NOT NULL,
    shortcuts TEXT DEFAULT '[]'
--    maybe change password to 60 bc bcrypt produces 60 char long outputs
);

CREATE TABLE notes (
    id BIGSERIAL PRIMARY KEY,
    author VARCHAR(32) NOT NULL,
    title VARCHAR(200) NOT NULL DEFAULT 'Untitled',
    body TEXT DEFAULT '' --draft js content
);