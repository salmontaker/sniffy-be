package com.salmontaker.sniffy.auth.exception;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

public class UnauthenticatedException extends AuthenticationCredentialsNotFoundException {
    public UnauthenticatedException() {
        super("인증 정보가 없습니다.");
    }
}
