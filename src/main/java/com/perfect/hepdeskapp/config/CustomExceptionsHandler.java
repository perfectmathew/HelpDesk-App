package com.perfect.hepdeskapp.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
@ControllerAdvice
public class CustomExceptionsHandler {
        @ExceptionHandler(value = {EmailExistsException.class})
        public ResponseEntity<Object> handleCustomException(EmailExistsException e){
            HttpStatus badRequest = HttpStatus.BAD_REQUEST;
            CustomException customException = new CustomException(
                    e.getMessage(),
                    e,
                    badRequest,
                    ZonedDateTime.now(ZoneId.of("Z"))
            );
            return new ResponseEntity<>(customException,badRequest);
        }

}

