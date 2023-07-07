package ua.ubki.cassmon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {/*
        CassandraAutoConfiguration.class,
        CassandraDataAutoConfiguration.class*/})
public class CassMonApplication {
    public static void main(String[] args) {
        SpringApplication.run(CassMonApplication.class, args);
    }
}
