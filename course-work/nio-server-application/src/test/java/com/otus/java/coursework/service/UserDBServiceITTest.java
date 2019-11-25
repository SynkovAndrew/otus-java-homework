package com.otus.java.coursework.service;

import com.otus.java.coursework.NioServerApplication;
import com.otus.java.coursework.dto.CreateUserRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(classes = NioServerApplication.class)
public class UserDBServiceITTest {
    @Autowired
    private UserDBService userDBService;

    @Test
    public void createTest() {
        userDBService.create(CreateUserRequestDTO.builder()
                .age(22)
                .name("Alexey")
                .build());
        final var users = userDBService.findAll();
        assertAll(
                () -> assertThat(users.get(0)).isNotNull(),
                () -> assertThat(users.get(0).getAge()).isEqualTo(22),
                () -> assertThat(users.get(0).getName()).isEqualTo("Alexey"),
                () -> assertThat(users.get(0).getUserId()).isNotNull()
        );
    }
}