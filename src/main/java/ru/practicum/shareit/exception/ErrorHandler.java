package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleConflictException(ConflictException e) {
        return ErrorResponseDto.builder()
                .error("Ошибка валидации данных")
                .description(e.getMessage())
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFoundException(NotFoundException e) {
        return ErrorResponseDto.builder()
                .error("Объект не найден")
                .description(e.getMessage())
                .build();
    }

    @ExceptionHandler(NotAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleNotAvailableException(NotAvailableException e) {
        return ErrorResponseDto.builder()
                .error("Вещь недоступна для бронирования")
                .description(e.getMessage())
                .build();
    }
}
