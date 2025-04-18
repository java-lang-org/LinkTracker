package backend.academy.scrapper.entity;

import backend.academy.scrapper.NotificationMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chat")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class ChatEntity {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_mode", nullable = false)
    private NotificationMode notificationMode;

    public ChatEntity(long id) {
        this.id = id;
        this.notificationMode = NotificationMode.IMMEDIATE;
    }
}
