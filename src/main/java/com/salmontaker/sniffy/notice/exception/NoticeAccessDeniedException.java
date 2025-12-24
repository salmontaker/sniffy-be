package com.salmontaker.sniffy.notice.exception;

import org.springframework.security.access.AccessDeniedException;

public class NoticeAccessDeniedException extends AccessDeniedException {
    public NoticeAccessDeniedException() {
        super("해당 유저의 알림이 아닙니다.");
    }
}
