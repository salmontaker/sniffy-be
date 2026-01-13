package com.salmontaker.sniffy.user.domain;

import com.salmontaker.sniffy.agency.domain.AgencyFavorite;
import com.salmontaker.sniffy.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String username;

    @Column(length = 255)
    private String password;

    @Column(length = 50)
    private String nickname;

    @OneToOne(mappedBy = "user")
    private UserPreference userPreference;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "user")
    private List<AgencyFavorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserKeyword> keywords = new ArrayList<>();

    private User(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public static User create(String username, String password, String nickname) {
        return new User(username, password, nickname);
    }

    public void update(String nickname, String password) {
        if (nickname != null) this.nickname = nickname;
        if (password != null) this.password = password;
    }
}
