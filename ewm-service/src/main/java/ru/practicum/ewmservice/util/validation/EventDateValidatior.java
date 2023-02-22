package ru.practicum.ewmservice.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventDateValidatior implements ConstraintValidator<EventDateValidation, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime time, ConstraintValidatorContext constraintValidatorContext) {
        return time != null && time.isAfter(LocalDateTime.now().plusHours(2));
    }
}
