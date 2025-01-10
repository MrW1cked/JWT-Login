package com.back.sousa.helpers;

import com.back.sousa.helpers.custom_interfaces.ValidCCNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CCNumberValidator implements ConstraintValidator<ValidCCNumber, String> {

    @Override
    public void initialize(ValidCCNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // or true, depending on whether null is considered valid
        }
        // Check if the value contains exactly 8 digits and is numeric
        return value.matches("^[0-9]{8}$");
    }
}