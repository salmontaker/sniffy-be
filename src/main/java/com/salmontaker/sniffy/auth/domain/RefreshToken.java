package com.salmontaker.sniffy.auth.domain;

import com.salmontaker.sniffy.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(length = 255)
    private String token;

    private RefreshToken(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public static RefreshToken create(User user, String token) {
        return new RefreshToken(user, token);
    }

    public void update(User user, String token) {
        if (user != null) this.user = user;
        if (token != null) this.token = token;
    }
}
