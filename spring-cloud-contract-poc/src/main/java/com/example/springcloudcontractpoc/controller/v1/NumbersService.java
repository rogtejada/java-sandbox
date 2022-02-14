package com.example.springcloudcontractpoc.controller.v1;

import org.springframework.stereotype.Service;

@Service
public class NumbersService {

  public Classification classificate(Integer number) {
    return number % 2 == 0 ? Classification.EVEN : Classification.ODD;
  }
}
