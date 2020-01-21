package com.otus.java.coursework.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"size"})
public class FindUsersResponseDTO implements Serializable {
    private List<UserDTO> content;
    private int size;
}
