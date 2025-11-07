package com.salmontaker.sniffy.notification.domain;

import com.salmontaker.sniffy.common.BaseEntity;
import com.salmontaker.sniffy.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    User user;

    @Column(length = 255)
    String title;

    @Lob    // TEXT
    String content;

    @Column
    LocalDateTime sentAt;

    private Notification(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }

    public static Notification create(User user, String title, String content) {
        return new Notification(user, title, content);
    }
}
