package com.salmontaker.sniffy.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "unique_user_id", columnNames = {"user_id"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column
    private Boolean isPushEnabled;

    @Column
    private Boolean isFavoriteFirst;

    private UserPreference(User user, Boolean isPushEnabled, Boolean isFavoriteFirst) {
        this.user = user;
        this.isPushEnabled = isPushEnabled;
        this.isFavoriteFirst = isFavoriteFirst;
    }

    public static UserPreference create(User user, Boolean isPushEnabled, Boolean isFavoriteFirst) {
        return new UserPreference(user, isPushEnabled, isFavoriteFirst);
    }

    public void update(Boolean isPushEnabled, Boolean isFavoriteFirst) {
        if (isPushEnabled != null) this.isPushEnabled = isPushEnabled;
        if (isFavoriteFirst != null) this.isFavoriteFirst = isFavoriteFirst;
    }
}
