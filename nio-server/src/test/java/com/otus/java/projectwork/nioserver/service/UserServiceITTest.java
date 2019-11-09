package com.otus.java.projectwork.nioserver.service;

import com.otus.java.projectwork.nioserver.NioServerApplication;
import com.otus.java.projectwork.nioserver.dto.CreateUserRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(classes = NioServerApplication.class)
public class UserServiceITTest {
    @Autowired
    private UserService userService;

    @Test
    public void createTest() {
        StepVerifier.create(
                userService.create(CreateUserRequestDTO.builder()
                        .age(22)
                        .name("Alexey")
                        .build()))
                .assertNext(obj -> assertAll(
                        () -> assertThat(obj).isNotNull(),
                        () -> assertThat(obj.getAge()).isEqualTo(22),
                        () -> assertThat(obj.getName()).isEqualTo("Alexey"),
                        () -> assertThat(obj.getUserId()).isNotNull()
                ))
                .expectComplete()
                .verify();
    }
}