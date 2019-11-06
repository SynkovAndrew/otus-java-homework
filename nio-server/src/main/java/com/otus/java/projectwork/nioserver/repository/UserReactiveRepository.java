package com.otus.java.projectwork.nioserver.repository;

import com.otus.java.projectwork.nioserver.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReactiveRepository extends ReactiveMongoRepository<User, String> {
}