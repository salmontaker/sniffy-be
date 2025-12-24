package com.salmontaker.sniffy.user.exception;

import java.util.NoSuchElementException;

public class UserNotFoundException extends NoSuchElementException {
    public UserNotFoundException() {
        super("사용자를 찾을 수 없습니다.");
    }
}
