package com.example.springcloudcontractpoc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.springcloudcontractpoc.controller.v1.Classification;
import com.example.springcloudcontractpoc.controller.v1.NumbersController;
import com.example.springcloudcontractpoc.controller.v1.NumbersService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;

public class MockMvcTest {


  @BeforeEach
  public void setUp() {
    RestAssuredMockMvc.standaloneSetup(new NumbersController(mockNumbers()));
  }

  private NumbersService mockNumbers() {
    NumbersService numbersServiceMock = mock(NumbersService.class);
    when(numbersServiceMock.classificate(2)).thenReturn(Classification.EVEN);
    when(numbersServiceMock.classificate(1)).thenReturn(Classification.ODD);

    return numbersServiceMock;
  }
}
