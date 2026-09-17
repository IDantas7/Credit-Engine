package br.com.srm.creditengine.api.exception;

import br.com.srm.creditengine.api.dto.response.ErrorResponse;
import br.com.srm.creditengine.domain.exception.ExchangeRateNotFoundException;
import br.com.srm.creditengine.domain.exception.IdempotencyConflictException;
import br.com.srm.creditengine.domain.exception.SimulationAlreadySettledException;
import br.com.srm.creditengine.domain.exception.SimulationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SimulationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSimulationNotFound(
            SimulationNotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                404,
                "Simulation Not Found",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<ErrorResponse> handleIdempotencyConflict(IdempotencyConflictException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                409,
                "Idempotency Conflict",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(SimulationAlreadySettledException.class)
    public ResponseEntity<ErrorResponse> handleSimulationAlreadySettled(
            SimulationAlreadySettledException exception){
        ErrorResponse errorResponse = new ErrorResponse(
                409,
                "Simulation Already Settled",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception){
        String message = exception.getBindingResult().getFieldError().getDefaultMessage();

        ErrorResponse errorResponse = new ErrorResponse(
                400,
                "Validation Error",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ExchangeRateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExchangeRateNotFound(
            ExchangeRateNotFoundException exception){
        ErrorResponse errorResponse = new ErrorResponse(
                422,
                "Exchange Rate Not Found",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception){
        ErrorResponse errorResponse = new ErrorResponse(
                400,
                "Message not readable",
                "O corpo da requisição contém dados inválidos",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
