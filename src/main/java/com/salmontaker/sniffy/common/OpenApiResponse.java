package com.salmontaker.sniffy.common;

import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class OpenApiResponse<T> {
    private Response<T> response;

    @Data
    public static class Response<T> {
        private Header header;
        private Body<T> body;
    }

    @Data
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    public static class Body<T> {
        private Items<T> items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;
    }

    @Data
    public static class Items<T> {
        private List<T> item;
    }
    
    public boolean isSuccess() {
        return Optional.ofNullable(getHeader())
                .map(Header::getResultCode)
                .map("00"::equals)
                .orElse(false);
    }

    public Header getHeader() {
        return Optional.ofNullable(response)
                .map(Response::getHeader)
                .orElse(null);
    }

    public Body<T> getBody() {
        return Optional.ofNullable(response)
                .map(Response::getBody)
                .orElse(null);
    }

    public List<T> getItems() {
        return Optional.ofNullable(response)
                .map(Response::getBody)
                .map(Body::getItems)
                .map(Items::getItem)
                .orElse(List.of());
    }
}