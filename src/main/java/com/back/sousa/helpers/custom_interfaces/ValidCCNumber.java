package com.back.sousa.helpers.custom_interfaces;

import com.back.sousa.helpers.CCNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CCNumberValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCCNumber {
    String message() default "ccNumber must be exactly 8 digits long";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}