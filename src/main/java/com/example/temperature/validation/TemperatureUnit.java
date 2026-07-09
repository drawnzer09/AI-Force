package com.example.temperature.validation;

import java.util.Arrays;

public enum TemperatureUnit {
    C,
    F,
    K;

    public static boolean isValid(String value) {
        return Arrays.stream(values()).anyMatch(unit -> unit.name().equals(value));
    }
}
