package com.otus.java.projectwork.nioserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDTO {
    @NotNull
    private Integer age;
    @NotEmpty
    private String name;
}
