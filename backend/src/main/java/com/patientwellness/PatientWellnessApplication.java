package com.patientwellness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PatientWellnessApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatientWellnessApplication.class, args);
    }
}
