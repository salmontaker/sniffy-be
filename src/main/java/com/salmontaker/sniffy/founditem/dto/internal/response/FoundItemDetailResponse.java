package com.salmontaker.sniffy.founditem.dto.internal.response;

import com.salmontaker.sniffy.founditem.dto.external.response.LostFoundDetailResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FoundItemDetailResponse {
    private String atcId;
    private String csteSteNm;
    private String depPlace;
    private String fdFilePathImg;
    private Integer fdHor;
    private String fdPlace;
    private String fdPrdtNm;
    private Integer fdSn;
    private String fdYmd;
    private String orgNm;
    private String prdtClNm;
    private String tel;
    private String uniq;

    public static FoundItemDetailResponse from(LostFoundDetailResponse response) {
        return FoundItemDetailResponse.builder()
                .atcId(response.getAtcId())
                .csteSteNm(response.getCsteSteNm())
                .depPlace(response.getDepPlace())
                .fdFilePathImg(response.getFdFilePathImg())
                .fdHor(response.getFdHor())
                .fdPlace(response.getFdPlace())
                .fdPrdtNm(response.getFdPrdtNm())
                .fdSn(response.getFdSn())
                .fdYmd(response.getFdYmd())
                .orgNm(response.getOrgNm())
                .prdtClNm(response.getPrdtClNm())
                .tel(response.getTel())
                .uniq(response.getUniq())
                .build();
    }
}
