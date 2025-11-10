package com.salmontaker.sniffy.notice.dto.response;

import com.salmontaker.sniffy.notice.domain.Notice;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoticeResponse {
    private Integer id;
    private String title;
    private String content;

    public static NoticeResponse from(Notice notice) {
        return NoticeResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .build();
    }
}
