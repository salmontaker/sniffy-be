package com.salmontaker.sniffy.founditem.domain;

import com.salmontaker.sniffy.agency.domain.Agency;
import com.salmontaker.sniffy.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(indexes = {
        @Index(name = "index_clr_nm", columnList = "clrNm"),
        @Index(name = "index_fd_ymd", columnList = "fdYmd"),
        @Index(name = "index_fd_prdt_nm", columnList = "fdPrdtNm"),
        @Index(name = "index_prdt_cl_nm", columnList = "prdtClNm"),
        @Index(name = "index_created_at", columnList = "createdAt"),
        @Index(name = "index_updated_at", columnList = "updatedAt"),
        @Index(name = "index_deleted_at", columnList = "deletedAt")},
        uniqueConstraints = {@UniqueConstraint(name = "unique_atc_id_fd_sn", columnNames = {"atcId", "fdSn"})}
)
@SQLRestriction("deleted_at IS NULL")
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
