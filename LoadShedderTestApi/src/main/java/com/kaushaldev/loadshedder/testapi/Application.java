package com.kaushaldev.loadshedder.testapi;

import com.kaushaldev.loadshedder.core.springbootconfiguration.LoadShedderSpringBootConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(LoadShedderSpringBootConfiguration.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
