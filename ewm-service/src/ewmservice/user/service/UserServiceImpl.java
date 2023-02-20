package ru.practicum.ewmservice.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.user.dto.UserDto;
import ru.practicum.ewmservice.user.dto.UserIncomeDto;
import ru.practicum.ewmservice.user.model.User;
import ru.practicum.ewmservice.user.storage.UserRepo;
import ru.practicum.ewmservice.user.util.UserMapper;
import ru.practicum.ewmservice.user.util.exceptions.EntityNotExistException;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Transactional
public class UserServiceImpl implements UserService{
    private final UserRepo userRepo;

    @Override
    public UserDto create(UserIncomeDto dto) {
        User user = userRepo.save(UserMapper.toUser(dto));
        log.info("Создан пользователь c id = {} ", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> get(List<Long> ids, int from, int size) {
        List<User> users;

        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size,
                Sort.by(Sort.Direction.ASC, "id")
        );

        if (ids.isEmpty()) {
            users = userRepo.findAll(pageable).toList();
            log.info("Возвращен список всех пользователей");
        } else {
            users = userRepo.findAllByIdIn(ids, pageable).toList();
            log.info("Возвращен список всех пользователей c id = {} ", ids);
        }

        return UserMapper.toUserDto(users);
    }

    @Override
    public void delete(long userId) {
        findOrThrow(userId);
        userRepo.deleteById(userId);
        log.info("Удален пользователь c id = {} ", userId);
    }

    private User findOrThrow(long userId) { // todo delete
        return userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Пользователь c id = %s не существует", userId))
                );
    }
}
