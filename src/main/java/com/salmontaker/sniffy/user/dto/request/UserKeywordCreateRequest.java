package com.salmontaker.sniffy.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserKeywordCreateRequest {
    @NotBlank
    private String keyword;
}
