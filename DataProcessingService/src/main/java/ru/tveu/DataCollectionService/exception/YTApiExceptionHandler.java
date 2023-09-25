package ru.tveu.DataCollectionService.exception;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.tveu.DataCollectionService.exception.url.UrlProcessorException;

import java.util.Objects;

@ControllerAdvice
public class YTApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = GoogleJsonResponseException.class)
    public ResponseEntity<String> handleGoogleJson(GoogleJsonResponseException ex) {

        return new ResponseEntity<>(ex.getMessage(),
                Objects.requireNonNull(HttpStatus.resolve(ex.getDetails().getCode())));
    }

    @ExceptionHandler(value = UrlProcessorException.class)
    public ResponseEntity<String> handleGoogleJson(UrlProcessorException ex) {

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}