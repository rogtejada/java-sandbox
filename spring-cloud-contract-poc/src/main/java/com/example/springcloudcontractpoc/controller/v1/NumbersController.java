package com.example.springcloudcontractpoc.controller.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/numbers")
public class NumbersController {

  private NumbersService service;

  public NumbersController(NumbersService service) {
    this.service = service;
  }

  @PostMapping("/classification/{number}")
  public ResponseEntity<NumberResponse> classificate(@PathVariable Integer number) {
    return ResponseEntity.ok(NumberResponse.builder()
        .classification(service.classificate(number))
        .build());
  }
}
