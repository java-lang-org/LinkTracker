package backend.academy.scrapper.service;

import backend.academy.scrapper.entity.TagEntity;
import java.util.List;

public interface TagService {
    List<TagEntity> addTags(List<String> tags);

    void deleteUnusedTags();
}
