package com.back.sousa.helpers;

import com.back.sousa.helpers.custom_interfaces.ValidCCNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CCNumberValidator implements ConstraintValidator<ValidCCNumber, Integer> {

    @Override
    public void initialize(ValidCCNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // or true, depending on whether null is considered valid
        }
        String valueAsString = value.toString();
        // Check if the value contains exactly 8 digits and is numeric
        return valueAsString.matches("^[0-9]{8}$");
    }
}