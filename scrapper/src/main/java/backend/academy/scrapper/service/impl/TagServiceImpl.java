package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.entity.TagEntity;
import backend.academy.scrapper.repository.TagRepository;
import backend.academy.scrapper.service.TagService;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TagServiceImpl implements TagService {
    private TagRepository tagRepository;

    @Override
    public List<TagEntity> addTags(List<String> tags) {
        return tags.stream().map(this::saveTag).toList();
    }

    @Override
    public void deleteUnusedTags() {
        tagRepository.deleteUnusedTags();
    }

    private TagEntity saveTag(String tag) {
        Optional<TagEntity> existingTagEntity = tagRepository.findByName(tag);
        return existingTagEntity.orElseGet(() -> {
            TagEntity newTagEntity = new TagEntity();
            newTagEntity.name(tag);
            return tagRepository.save(newTagEntity);
        });
    }
}
