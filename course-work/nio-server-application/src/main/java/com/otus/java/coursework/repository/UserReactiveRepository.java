package com.otus.java.coursework.repository;

import com.otus.java.coursework.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReactiveRepository extends ReactiveMongoRepository<User, String> {
}