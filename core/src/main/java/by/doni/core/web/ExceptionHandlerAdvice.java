package by.doni.core.web;

import by.doni.core.exception.PrattlersException;
import by.doni.core.web.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    @EventListener(PrattlersException.class)
    public ResponseEntity<ErrorResponse> prattlersExceptionHandler(PrattlersException exception) {
        log.error("Prattlers exception", exception);
        return response(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String message) {
        var errorResponseBody = ErrorResponse.builder()
                .message(message)
                .build();
        return ResponseEntity.status(status).body(errorResponseBody);
    }
}
