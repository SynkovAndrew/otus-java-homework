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
        final var obj = userDBService.create(CreateUserRequestDTO.builder()
                .age(22)
                .name("Alexey")
                .build());
        assertAll(
                () -> assertThat(obj).isNotNull(),
                () -> assertThat(obj.getAge()).isEqualTo(22),
                () -> assertThat(obj.getName()).isEqualTo("Alexey"),
                () -> assertThat(obj.getUserId()).isNotNull()
        );
    }
}