package repository;

import domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReactiveRepository extends ReactiveMongoRepository<User, String> {
}