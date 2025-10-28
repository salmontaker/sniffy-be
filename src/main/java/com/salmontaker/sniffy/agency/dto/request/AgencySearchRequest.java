package com.salmontaker.sniffy.agency.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgencySearchRequest {
    private BigDecimal minLatitude;
    private BigDecimal maxLatitude;
    private BigDecimal minLongitude;
    private BigDecimal maxLongitude;
}
