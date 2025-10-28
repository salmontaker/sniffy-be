package com.salmontaker.sniffy.agency.domain;

import com.salmontaker.sniffy.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgencyFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Agency agency;

    private AgencyFavorite(User user, Agency agency) {
        this.user = user;
        this.agency = agency;
    }

    public static AgencyFavorite create(User user, Agency agency) {
        return new AgencyFavorite(user, agency);
    }
}
