CREATE INDEX idx_link_url ON link (url);
CREATE INDEX idx_link_type ON link (type);
CREATE INDEX idx_link_last_update ON link (last_update);

CREATE INDEX idx_tag_name ON tag (name);

CREATE INDEX idx_chat_link_chat_id ON chat_link (chat_id);
CREATE INDEX idx_chat_link_link_id ON chat_link (link_id);

CREATE INDEX idx_chat_link_tag_chat_id ON chat_link_tag (chat_id);
CREATE INDEX idx_chat_link_tag_link_id ON chat_link_tag (link_id);

CREATE INDEX idx_chat_link_filter_chat_id ON chat_link_filter (chat_id);
CREATE INDEX idx_chat_link_filter_link_id ON chat_link_filter (link_id);
