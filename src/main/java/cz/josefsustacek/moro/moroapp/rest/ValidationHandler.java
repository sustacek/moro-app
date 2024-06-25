package cz.josefsustacek.moro.moroapp.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

/*
 Various arguments of REST controller methods are marked with @Valid. This class makes sure
 a user-friendly error messages are displayed to the users, so that they can fix their data.
 <br/>
 Inspired by:
 https://www.javaguides.net/2021/03/validation-in-spring-boot-rest-api-with-hibernate-validator.html
 */
@ControllerAdvice
public class ValidationHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        // One fields may have multiple errors, so we need a merge function as well
        Map<String, String> errorsByField =
                ex.getBindingResult().getAllErrors()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        oe -> ((FieldError) oe).getField(),
                                        ObjectError::getDefaultMessage,
                                        (e1, e2) -> e1 + " & " + e2)
                        );

        return new ResponseEntity<>(errorsByField, HttpStatus.BAD_REQUEST);
    }

}
