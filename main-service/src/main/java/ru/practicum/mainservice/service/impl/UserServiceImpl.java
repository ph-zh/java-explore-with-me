package ru.practicum.mainservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.user.UserFullDto;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.mapper.UserMapper;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.pagination.OffsetBasedPageRequest;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserFullDto addUser(UserFullDto userFullDto) {
        User user = userRepository.save(UserMapper.toUser(userFullDto));

        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserFullDto> getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = new OffsetBasedPageRequest(from, size, Sort.by(Sort.Direction.DESC, "id"));

        return ids != null ?
                userRepository.findAllByIdIn(ids, pageable).getContent().stream()
                            .map(UserMapper::toUserDto).collect(Collectors.toList()) :
                userRepository.findAll(pageable).stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }

        userRepository.deleteById(userId);
    }
}
