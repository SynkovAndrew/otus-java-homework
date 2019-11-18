package com.otus.java.coursework.repository;

import com.otus.java.coursework.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}