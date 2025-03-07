package backend.academy.scrapper;

import backend.academy.scrapper.entity.TagEntity;
import backend.academy.scrapper.repository.TagRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TagService {
    private TagRepository tagRepository;

    @Transactional
    public List<TagEntity> addTags(List<String> tags) {
        return tags.stream().map(this::saveTag).toList();
    }

    @Transactional
    public void deleteUnused() {
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
