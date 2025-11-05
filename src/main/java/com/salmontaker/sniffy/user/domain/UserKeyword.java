package com.salmontaker.sniffy.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(length = 255)
    private String keyword;

    private UserKeyword(User user, String keyword) {
        this.user = user;
        this.keyword = keyword;
    }

    public static UserKeyword create(User user, String keyword) {
        return new UserKeyword(user, keyword);
    }

    public void update(String keyword) {
        this.keyword = keyword;
    }
}
