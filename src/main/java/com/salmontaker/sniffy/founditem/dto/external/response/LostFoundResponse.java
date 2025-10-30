package com.salmontaker.sniffy.founditem.dto.external.response;

import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class LostFoundResponse {
    private Response response;

    @Data
    public static class Response {
        private Header header;
        private Body body;
    }

    @Data
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    public static class Body {
        private Items items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;
    }

    @Data
    public static class Items {
        private List<Item> item;
    }

    @Data
    public static class Item {
        private String atcId;
        private String clrNm;
        private String depPlace;
        private String fdFilePathImg;
        private String fdPrdtNm;
        private String fdSbjt;
        private int fdSn;
        private String fdYmd;
        private String prdtClNm;
        private int rnum;
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

    public Body getBody() {
        return Optional.ofNullable(response)
                .map(Response::getBody)
                .orElse(null);
    }

    public List<Item> getItems() {
        return Optional.ofNullable(response)
                .map(Response::getBody)
                .map(Body::getItems)
                .map(Items::getItem)
                .orElse(List.of());
    }
}