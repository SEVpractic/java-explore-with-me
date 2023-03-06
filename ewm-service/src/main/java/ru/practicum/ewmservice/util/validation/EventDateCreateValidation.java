package ru.practicum.ewmservice.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventDateCreateValidator.class)
public @interface EventDateCreateValidation {
    String message() default "Время начала не может быть раньше чем через два часа после публикации";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
