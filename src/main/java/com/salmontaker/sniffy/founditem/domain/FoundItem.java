package com.salmontaker.sniffy.founditem.domain;

import com.salmontaker.sniffy.agency.domain.Agency;
import com.salmontaker.sniffy.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "unique_atc_id_fd_sn", columnNames = {"atcId", "fdSn"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoundItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Agency agency;

    @Column(length = 50)
    private String atcId;

    @Column(length = 100)
    private String clrNm;

    @Column(length = 100)
    private String fdFilePathImg;

    @Column(length = 200)
    private String fdPrdtNm;

    @Column(length = 300)
    private String fdSbjt;

    @Column
    private Integer fdSn;

    @Column
    private LocalDate fdYmd;

    @Column(length = 100)
    private String prdtClNm;
}
