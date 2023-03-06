package ru.practicum.ewmservice.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.user.dto.UserDto;
import ru.practicum.ewmservice.user.dto.UserIncomeDto;
import ru.practicum.ewmservice.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public List<UserDto> get(@RequestParam(name = "ids", required = false) List<Long> ids,
                             @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                             @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        return userService.get(ids, from, size);
    }

    @DeleteMapping(path = "/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("userId") @Positive long userId) {
        userService.delete(userId);
    }
}
