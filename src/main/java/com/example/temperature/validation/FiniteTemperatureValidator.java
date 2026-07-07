package com.example.temperature.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FiniteTemperatureValidator implements ConstraintValidator<FiniteTemperature, Double> {

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        return value == null || Double.isFinite(value);
    }
}
