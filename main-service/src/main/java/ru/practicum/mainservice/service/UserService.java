package ru.practicum.mainservice.service;

import ru.practicum.mainservice.dto.user.UserFullDto;

import java.util.List;

public interface UserService {

    UserFullDto addUser(UserFullDto userFullDto);

    List<UserFullDto> getUsers(List<Long> ids, int from, int size);

    void deleteUser(long userId);
}
