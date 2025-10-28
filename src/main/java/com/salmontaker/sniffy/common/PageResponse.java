package com.salmontaker.sniffy.common;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    private final int number;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final List<T> content;

    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .content(page.getContent())
                .build();
    }
}
