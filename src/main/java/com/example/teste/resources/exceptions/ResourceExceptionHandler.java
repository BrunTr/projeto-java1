package com.example.teste.resources.exceptions;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.teste.services.exceptions.DatabaseException;
import com.example.teste.services.exceptions.IllegalArgumentException;
import com.example.teste.services.exceptions.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ResourceExceptionHandler {

		@ExceptionHandler(ResourceNotFoundException.class)
		public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
			String error = "Resource not found";
			HttpStatus status = HttpStatus.NOT_FOUND;
			StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
			return ResponseEntity.status(status).body(err);
		
	}
		@ExceptionHandler(IllegalArgumentException.class)
	    public ResponseEntity<StandardError> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
			String error = "Bad Request";
			HttpStatus status = HttpStatus.BAD_REQUEST;
			StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
			return ResponseEntity.status(status).body(err);
	   }
		
		@ExceptionHandler(DatabaseException.class)
		public ResponseEntity<StandardError> database(DatabaseException e, HttpServletRequest request) {
			String error = "Database error";
			HttpStatus status = HttpStatus.BAD_REQUEST;
			StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
			return ResponseEntity.status(status).body(err);
		}
	   
		@ExceptionHandler(MethodArgumentNotValidException.class)
	    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
	        
	        Map<String, String> errors = new HashMap<>();

	        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
	            errors.put(error.getField(), error.getDefaultMessage());
	        }

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	    }

}
