package com.salmontaker.sniffy.common.exception;

public class ExternalApiException extends RuntimeException {
    public ExternalApiException() {
        super("외부 API 요청중 오류가 발생했습니다.");
    }
}
