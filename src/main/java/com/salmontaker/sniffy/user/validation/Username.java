package com.salmontaker.sniffy.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "^[A-Za-z0-9]{4,}$", message = "영문과 숫자만 사용 가능하며, 최소 4자 이상이어야 합니다.")
public @interface Username {
    String message() default "영문과 숫자만 사용 가능하며, 최소 4자 이상이어야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
