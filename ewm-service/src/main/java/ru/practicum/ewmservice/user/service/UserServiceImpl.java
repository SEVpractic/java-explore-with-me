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
import ru.practicum.ewmservice.util.UtilService;
import ru.practicum.ewmservice.util.mappers.UserMapper;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final UtilService utilService;

    @Override
    @Transactional
    public UserDto create(UserIncomeDto dto) {
        User user = userRepo.save(UserMapper.toUser(dto));
        log.info("Создан пользователь c id = {} ", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> get(List<Long> ids, int from, int size) {
        List<User> users;

        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size,
                Sort.by(Sort.Direction.ASC, "id")
        );

        if (ids == null) {
            users = userRepo.findAll(pageable).toList();
            log.info("Возвращен список всех пользователей");
        } else {
            users = userRepo.findAllByIdIn(ids, pageable).toList();
            log.info("Возвращен список всех пользователей c id = {} ", ids);
        }

        return UserMapper.toUserDto(users);
    }

    @Override
    @Transactional
    public void delete(long userId) {
        utilService.findUserOrThrow(userId);
        userRepo.deleteById(userId);
        log.info("Удален пользователь c id = {} ", userId);
    }
}
