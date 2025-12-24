package com.salmontaker.sniffy.auth.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class InvalidCredentialsException extends BadCredentialsException {
    public InvalidCredentialsException() {
        super("아이디 또는 비밀번호가 올바르지 않습니다.");
    }
}
