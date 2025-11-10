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

    private Notice(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }

    public static Notice create(User user, String title, String content) {
        return new Notice(user, title, content);
    }
}
