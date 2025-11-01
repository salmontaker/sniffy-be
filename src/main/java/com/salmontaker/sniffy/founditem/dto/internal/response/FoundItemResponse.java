package com.salmontaker.sniffy.founditem.dto.internal.response;

import com.salmontaker.sniffy.founditem.domain.FoundItem;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class FoundItemResponse {
    private Integer id;
    private String agencyName;
    private String atcId;
    private String clrNm;
    private String fdFilePathImg;
    private String fdPrdtNm;
    private String fdSbjt;
    private Integer fdSn;
    private LocalDate fdYmd;
    private String prdtClNm;

    public static FoundItemResponse from(FoundItem foundItem) {
        return FoundItemResponse.builder()
                .id(foundItem.getId())
                .agencyName(foundItem.getAgency().getName())
                .atcId(foundItem.getAtcId())
                .clrNm(foundItem.getClrNm())
                .fdFilePathImg(foundItem.getFdFilePathImg())
                .fdPrdtNm(foundItem.getFdPrdtNm())
                .fdSbjt(foundItem.getFdSbjt())
                .fdSn(foundItem.getFdSn())
                .fdYmd(foundItem.getFdYmd())
                .prdtClNm(foundItem.getPrdtClNm())
                .build();
    }
}
