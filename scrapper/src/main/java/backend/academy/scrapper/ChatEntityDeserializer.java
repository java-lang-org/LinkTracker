package backend.academy.scrapper;

import backend.academy.scrapper.entity.ChatEntity;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class ChatEntityDeserializer extends JsonDeserializer<ChatEntity> {
    @Override
    public ChatEntity deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        long id = node.get("id").asLong();

        String modeStr = node.get("notification_mode").asText();
        NotificationMode mode = NotificationMode.valueOf(modeStr.toUpperCase());

        return new ChatEntity(id, mode);
    }
}
