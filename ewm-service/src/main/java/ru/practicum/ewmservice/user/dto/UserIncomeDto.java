package ru.practicum.ewmservice.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Builder(toBuilder = true)
@Getter
@RequiredArgsConstructor
public class UserIncomeDto {
    @NotBlank
    private final String name;
    @Email
    @NotEmpty
    private final String email;
}
