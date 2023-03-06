package ru.practicum.ewmservice.util;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.springframework.lang.NonNull;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.deserializers(new LocalDateTimeDeserializer(LOCAL_DATE_TIME_FORMATTER));
    }

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        WebMvcConfigurer.super.addFormatters(registry);
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setDateTimeFormatter(LOCAL_DATE_TIME_FORMATTER);
        registrar.registerFormatters(registry);
    }
}
