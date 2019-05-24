package io.chucknorris.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.LinkedHashMap;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ConstraintViolationException.class})
    protected ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException exception, ServletWebRequest request
    ) {
        switch (request.getHeader(HttpHeaders.ACCEPT)) {
            case MediaType.TEXT_PLAIN_VALUE:
                StringBuilder stringBuilder = new StringBuilder();
                for (ConstraintViolation violation : exception.getConstraintViolations()) {
                    stringBuilder.append(
                            violation.getPropertyPath().toString() + ": " + violation.getMessage() + '\n'
                    );
                }

                return handleExceptionInternal(
                        exception,
                        stringBuilder.toString(),
                        new HttpHeaders(),
                        HttpStatus.BAD_REQUEST,
                        request
                );
            default:
                LinkedHashMap<String, Object> constraintViolations = new LinkedHashMap<>();
                for (ConstraintViolation violation : exception.getConstraintViolations()) {
                    constraintViolations.put(violation.getPropertyPath().toString(), violation.getMessage());
                }

                LinkedHashMap<String, Object> body = new LinkedHashMap<>();
                body.put("timestamp", new Date());
                body.put("status", HttpStatus.BAD_REQUEST.value());
                body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
                body.put("message", exception.getMessage());
                body.put("violations", constraintViolations);

                return handleExceptionInternal(
                        exception,
                        body,
                        new HttpHeaders(),
                        HttpStatus.BAD_REQUEST,
                        request
                );
        }
    }
}
