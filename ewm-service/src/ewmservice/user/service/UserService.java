package ru.practicum.ewmservice.user.service;

import ru.practicum.ewmservice.user.dto.UserDto;
import ru.practicum.ewmservice.user.dto.UserIncomeDto;

import java.util.List;

public interface UserService {
    UserDto create(UserIncomeDto dto);

    List<UserDto> get(List<Long> ids, int from, int size);

    void delete(long userId);
}
