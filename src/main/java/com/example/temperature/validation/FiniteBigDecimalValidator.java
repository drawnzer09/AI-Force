package com.example.temperature.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class FiniteBigDecimalValidator implements ConstraintValidator<FiniteBigDecimal, BigDecimal> {

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        return value == null || value.toString().equals(value.toPlainString());
    }
}
