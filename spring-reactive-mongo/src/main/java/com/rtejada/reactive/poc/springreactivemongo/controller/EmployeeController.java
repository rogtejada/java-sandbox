package com.rtejada.reactive.poc.springreactivemongo.controller;

import com.rtejada.reactive.poc.springreactivemongo.exception.ResultNotFoundException;
import com.rtejada.reactive.poc.springreactivemongo.model.Employee;
import com.rtejada.reactive.poc.springreactivemongo.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.Duration;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

	private final EmployeeRepository employeeRepository;

	public EmployeeController(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	@GetMapping()
	@ResponseStatus(code = HttpStatus.OK)
	public Flux<Employee> getAll(){
		return employeeRepository.findAll();
	}

	@GetMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public Mono<Employee> getById(@PathVariable("id") final String id){
		return employeeRepository.findById(id).switchIfEmpty(Mono.error(ResultNotFoundException::new));
	}

	@GetMapping(path = "/stream", produces = "application/stream+json")
	@ResponseStatus(code = HttpStatus.OK)
	public Flux<Employee> getAllStream() {
		return employeeRepository.findAll().delayElements(Duration.ofMillis(2000));
	}

	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public Mono<Employee> save(@Valid @RequestBody final Mono<Employee> employee){
		return employee.flatMap(employeeRepository::save);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public Mono<Void> save(@PathVariable("id") final String id){
		return employeeRepository.deleteById(id);
	}
}
