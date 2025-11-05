package com.salmontaker.sniffy.user.dto.response;

import com.salmontaker.sniffy.user.domain.UserKeyword;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserKeywordResponse {
    private int id;
    private String keyword;

    public static UserKeywordResponse from(UserKeyword keyword) {
        return UserKeywordResponse.builder()
                .id(keyword.getId())
                .keyword(keyword.getKeyword())
                .build();
    }
}
