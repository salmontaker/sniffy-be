package com.salmontaker.sniffy.agency.dto.response;

import com.salmontaker.sniffy.agency.domain.Agency;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AgencyResponse {
    private Integer id;
    private String name;
    private String address;
    private String telNo;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean isFavorite;

    public static AgencyResponse from(Agency agency, Boolean isFavorite) {
        return AgencyResponse.builder()
                .id(agency.getId())
                .name(agency.getName())
                .address(agency.getAddress())
                .telNo(agency.getTelNo())
                .latitude(agency.getLatitude())
                .longitude(agency.getLongitude())
                .isFavorite(isFavorite)
                .build();
    }
}
