package com.salmontaker.sniffy.founditem.dto.internal.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FoundItemRequest {
    private Integer id;
    private String agencyName;
    private String clrNm;
    private String fdPrdtNm;
    private String prdtClNm;
    private LocalDate startDate;
    private LocalDate endDate;
}
