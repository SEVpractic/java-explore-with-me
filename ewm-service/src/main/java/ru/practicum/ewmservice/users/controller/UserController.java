package ru.practicum.ewmservice.users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.users.dto.UserDto;
import ru.practicum.ewmservice.users.dto.UserIncomeDto;
import ru.practicum.ewmservice.users.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserIncomeDto dto) {
        return userService.create(dto);
    }

    @GetMapping
    public List<UserDto> get(@RequestParam(name = "ids", defaultValue = "") List<Long> ids,
                             @RequestParam(name = "from", defaultValue = "0") int from,
                             @RequestParam(name = "size", defaultValue = "10") int size) {
        return userService.get(ids, from, size); //В случае, если по заданным фильтрам не найдено ни одного пользователя, возвращает пустой список
    }

    @DeleteMapping(path = "/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("userId") @Positive long userId) {
        userService.delete(userId);
    }
}
