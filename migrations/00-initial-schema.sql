CREATE TABLE chat (
    id BIGINT PRIMARY KEY
);

CREATE TABLE link (
   id BIGSERIAL PRIMARY KEY,
   url TEXT NOT NULL UNIQUE
);

CREATE TABLE tag (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE filter (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    pattern TEXT NOT NULL,
    UNIQUE (name, pattern)
);

CREATE TABLE chat_link (
    chat_id BIGINT REFERENCES chat (id),
    link_id BIGINT REFERENCES link (id),
    PRIMARY KEY (chat_id, link_id)
);

CREATE TABLE chat_link_tag (
    chat_id BIGINT REFERENCES chat (id),
    link_id BIGINT REFERENCES link (id),
    tag_id BIGINT REFERENCES tag (id),
    PRIMARY KEY (chat_id, link_id, tag_id)
);

CREATE TABLE chat_link_filter (
    chat_id BIGINT REFERENCES chat (id),
    link_id BIGINT REFERENCES link (id),
    filter_id BIGINT REFERENCES filter (id),
    PRIMARY KEY (chat_id, link_id, filter_id)
);
