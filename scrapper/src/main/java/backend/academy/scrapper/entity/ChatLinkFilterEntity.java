package backend.academy.scrapper.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chat_link_filter")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class ChatLinkFilterEntity {
    @EmbeddedId
    private ChatLinkFilterId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatId")
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chatEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("linkId")
    @JoinColumn(name = "link_id", nullable = false)
    private LinkEntity linkEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("filterId")
    @JoinColumn(name = "filter_id", nullable = false)
    private FilterEntity filterEntity;
}
