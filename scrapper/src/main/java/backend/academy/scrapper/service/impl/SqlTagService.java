package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.entity.TagEntity;
import backend.academy.scrapper.service.TagService;
import java.util.List;

public class SqlTagService implements TagService {
    @Override
    public List<TagEntity> addTags(List<String> tags) {
        return List.of();
    }

    @Override
    public void deleteUnusedTags() {}
}
