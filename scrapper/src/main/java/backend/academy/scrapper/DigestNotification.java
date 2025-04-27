package backend.academy.scrapper;

import java.util.List;

public record DigestNotification(String description, List<Long> chatIds) {}
