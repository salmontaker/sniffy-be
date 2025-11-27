package com.salmontaker.sniffy.user.domain;

import com.salmontaker.sniffy.agency.domain.AgencyFavorite;
import com.salmontaker.sniffy.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @OneToMany(mappedBy = "user")
    private List<AgencyFavorite> favorites = new ArrayList<>();

    private User(String username, String password, String nickname) {
        // 아이디와 비밀번호는 null을 허용 (OAuth 계정 추가 예정)
        if (nickname == null) {
            throw new IllegalArgumentException("Nickname is null");
        }

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
