package com.salmontaker.sniffy.push.exception;

import java.util.NoSuchElementException;

public class PushSubscriptionNotFoundException extends NoSuchElementException {
    public PushSubscriptionNotFoundException() {
        super("구독정보를 찾을 수 없습니다.");
    }
}
