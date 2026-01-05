package com.salmontaker.sniffy.agency.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = @Index(name = "index_name", columnList = "name"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Agency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100)
    private String name;

    @Column(length = 200)
    private String address;

    @Column(length = 50)
    private String tel;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @OneToMany(mappedBy = "agency")
    private List<AgencyFavorite> favorites = new ArrayList<>();
}
