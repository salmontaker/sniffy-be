package com.salmontaker.sniffy.notice.domain;

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
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(length = 255)
    private String title;

    @Lob    // TEXT
    private String content;

    @Column
    private LocalDateTime sentAt;

    private Notice(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }

    public static Notice create(User user, String title, String content) {
        return new Notice(user, title, content);
    }

    public void sentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
