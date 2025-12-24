package com.salmontaker.sniffy.founditem.exception;

import java.util.NoSuchElementException;

public class FoundItemNotFoundException extends NoSuchElementException {
    public FoundItemNotFoundException() {
        super("습득물을 찾을 수 없습니다.");
    }
}
