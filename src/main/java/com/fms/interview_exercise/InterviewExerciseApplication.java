package com.fms.interview_exercise;

import com.fms.interview_exercise.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableAutoConfiguration()
@EnableConfigurationProperties({ApplicationProperties.class, LiquibaseProperties.class})
public class InterviewExerciseApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewExerciseApplication.class, args);
    }
}
