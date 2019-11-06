package com.otus.java.projectwork.nioserver.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@NoArgsConstructor
public class UserDTO {
    private Integer age;
    private String name;
    private Long userId;
}
