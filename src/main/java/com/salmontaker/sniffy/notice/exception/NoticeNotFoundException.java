package com.salmontaker.sniffy.notice.exception;

import java.util.NoSuchElementException;

public class NoticeNotFoundException extends NoSuchElementException {
    public NoticeNotFoundException() {
        super("알림을 찾을 수 없습니다.");
    }
}
