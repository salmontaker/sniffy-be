package com.salmontaker.sniffy.user.exception;

import java.util.NoSuchElementException;

public class UserPreferenceNotFoundException extends NoSuchElementException {
    public UserPreferenceNotFoundException() {
        super("사용자 설정을 찾을 수 없습니다.");
    }
}
