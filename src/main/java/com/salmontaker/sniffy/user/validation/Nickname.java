package com.salmontaker.sniffy.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,}$", message = "특수문자와 공백을 제외하고, 최소 2자 이상이어야 합니다.")
public @interface Nickname {
    String message() default "특수문자와 공백을 제외하고, 최소 2자 이상이어야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
