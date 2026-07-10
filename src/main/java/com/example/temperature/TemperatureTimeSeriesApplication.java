package com.example.temperature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TemperatureTimeSeriesApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemperatureTimeSeriesApplication.class, args);
    }
}
