package ru.practicum.ewmservice.users.service;

import ru.practicum.ewmservice.users.dto.UserDto;
import ru.practicum.ewmservice.users.dto.UserIncomeDto;

import java.util.List;

public interface UserService {
    UserDto create(UserIncomeDto dto);

    List<UserDto> get(List<Long> ids, int from, int size);

    void delete(long userId);
}
