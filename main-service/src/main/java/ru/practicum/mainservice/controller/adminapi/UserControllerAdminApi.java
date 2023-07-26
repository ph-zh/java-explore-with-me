package ru.practicum.mainservice.controller.adminapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.user.UserFullDto;
import ru.practicum.mainservice.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
public class UserControllerAdminApi {
    private final UserService userService;

    @PostMapping("/users")
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserFullDto addUser(@RequestBody @Valid UserFullDto userFullDto) {
        return userService.addUser(userFullDto);
    }

    @GetMapping("/users")
    public List<UserFullDto> getUsers(@RequestParam List<Long> ids,
                                      @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                      @RequestParam(defaultValue = "10") @Positive int size) {
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @Positive long userId) {
        userService.deleteUser(userId);
    }
}
