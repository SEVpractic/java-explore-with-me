package ru.practicum.ewmservice.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder(toBuilder = true)
@Getter
@RequiredArgsConstructor
public class UserDto {
    private final Long id;
    private final String name;
    private final String email;
}
