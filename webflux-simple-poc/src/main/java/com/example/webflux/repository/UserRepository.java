package com.example.webflux.repository;

import com.example.webflux.domain.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Integer> {

  Mono<User> findByUsername(String username);
}
