package ru.practicum.ewmservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.practicum.statsclient.StatsClientImpl;

@SpringBootApplication
@Import({StatsClientImpl.class})
public class EwmServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EwmServiceApplication.class, args);
    }

}
