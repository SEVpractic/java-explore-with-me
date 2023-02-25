package ru.practicum.ewmservice.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventDateUpdateValidator.class)
public @interface EventDateUpdateValidation {
    String message() default "Время начала не может быть раньше чем через час после публикации";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
