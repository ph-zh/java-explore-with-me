package ru.practicum.mainservice.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainservice.dto.user.UserFullDto;
import ru.practicum.mainservice.dto.user.UserShortDto;
import ru.practicum.mainservice.model.User;

@UtilityClass
public class UserMapper {

    public static User toUser(UserFullDto userFullDto) {
        return User.builder()
                .name(userFullDto.getName())
                .email(userFullDto.getEmail())
                .build();
    }

    public static UserFullDto toUserDto(User user) {
        return UserFullDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
