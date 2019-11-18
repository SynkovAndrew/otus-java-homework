package com.otus.java.coursework.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name = "USER")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Column(name = "AGE", nullable = false)
    private Integer age;
    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "USER_ID", nullable = false)
    private Long userId;
}
