package com.otus.java.ioc.web.controller;

import com.google.gson.Gson;
import com.otus.java.ioc.dto.CreateUserRequestDTO;
import com.otus.java.ioc.service.UserService;
import domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final Gson gson;

    @GetMapping("/user")
    public String loadUsers() {
        return gson.toJson(userService.loadAll());
    }

    @PostMapping("/user")
    public String create(@RequestBody final CreateUserRequestDTO request) {
        final User user = User.builder()
                .name(request.getName())
                .age(request.getAge())
                .build();
        userService.create(user);
        return gson.toJson(user);
    }
}
