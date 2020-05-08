package com.rtejada.reactive.poc.springreactivemongo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ResultNotFoundException extends RuntimeException {
	public ResultNotFoundException(String s) {
		super(s);
	}

	public ResultNotFoundException() {

	}
}
