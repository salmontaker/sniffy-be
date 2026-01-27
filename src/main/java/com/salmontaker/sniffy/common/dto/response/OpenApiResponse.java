package com.salmontaker.sniffy.common.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class OpenApiResponse<T> {
    private Header header;
    private Body<T> body;

    @Data
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    public static class Body<T> {
        private List<T> items;
        private Integer numOfRows;
        private Integer pageNo;
        private Integer totalCount;
    }

    public boolean isSuccess() {
        return Optional.ofNullable(header)
                .map(Header::getResultCode)
                .map("00"::equals)
                .orElse(false);
    }

    public List<T> getItems() {
        return Optional.ofNullable(body)
                .map(Body::getItems)
                .orElse(List.of());
    }

    public T getItem() {
        return Optional.ofNullable(body)
                .map(item -> item.items.get(0))
                .orElse(null);
    }
}