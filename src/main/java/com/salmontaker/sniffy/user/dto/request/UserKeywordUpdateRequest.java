package com.salmontaker.sniffy.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserKeywordUpdateRequest {
    @NotBlank
    private String keyword;
}
