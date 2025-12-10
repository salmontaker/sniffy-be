package com.salmontaker.sniffy.push.domain;

import com.salmontaker.sniffy.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "unique_endpoint", columnNames = {"endpoint"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(length = 500)
    private String endpoint;

    @Column(length = 100)
    private String p256dh;

    @Column(length = 50)
    private String auth;

    private PushSubscription(User user, String endpoint, String p256dh, String auth) {
        this.user = user;
        this.endpoint = endpoint;
        this.p256dh = p256dh;
        this.auth = auth;
    }

    public static PushSubscription create(User user, String endpoint, String p256dh, String auth) {
        return new PushSubscription(user, endpoint, p256dh, auth);
    }

    public void update(User user, String endpoint, String p256dh, String auth) {
        if (user != null) this.user = user;
        if (endpoint != null) this.endpoint = endpoint;
        if (p256dh != null) this.p256dh = p256dh;
        if (auth != null) this.auth = auth;
    }
}
