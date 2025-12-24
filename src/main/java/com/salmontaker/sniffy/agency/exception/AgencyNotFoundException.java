package com.salmontaker.sniffy.agency.exception;

import java.util.NoSuchElementException;

public class AgencyNotFoundException extends NoSuchElementException {
    public AgencyNotFoundException() {
        super("센터를 찾을 수 없습니다.");
    }
}
