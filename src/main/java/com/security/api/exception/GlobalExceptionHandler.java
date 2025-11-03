package com.security.api.exception;
import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import java.util.Map;
@RestControllerAdvice public class GlobalExceptionHandler {
  @ExceptionHandler(Exception.class)public ResponseEntity<?> ex(Exception e){return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error",e.getMessage()));}}