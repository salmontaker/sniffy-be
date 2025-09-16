package com.salmontaker.sniffy.user.domain;

import com.salmontaker.sniffy.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String password;
    private String nickname;

    private User(String email, String password, String nickname) {
        // 이메일과 비밀번호는 null을 허용 (OAuth 계정)
        if (nickname == null) {
            throw new IllegalArgumentException("Nickname is null");
        }

        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public static User create(String email, String password, String nickname) {
        return new User(email, password, nickname);
    }

    public void update(String email, String password, String nickname) {
        if (email != null) this.email = email;
        if (password != null) this.password = password;
        if (nickname != null) this.nickname = nickname;
    }
}
