package com.salmontaker.sniffy.user.exception;

public class DuplicateUserException extends IllegalStateException {
    public DuplicateUserException() {
        super("중복된 아이디가 있습니다.");
    }
}
