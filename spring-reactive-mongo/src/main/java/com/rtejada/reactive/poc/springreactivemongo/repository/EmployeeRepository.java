package com.rtejada.reactive.poc.springreactivemongo.repository;

import com.rtejada.reactive.poc.springreactivemongo.model.Employee;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface EmployeeRepository extends ReactiveMongoRepository<Employee, String> {
}
