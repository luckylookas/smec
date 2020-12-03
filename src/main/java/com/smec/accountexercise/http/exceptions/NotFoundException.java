package com.smec.accountexercise.http.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "ID Not Found")
public class NotFoundException extends RuntimeException {
}
