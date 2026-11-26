package ch.no1hardy.service.exception;

import java.time.LocalDateTime;

public record ApiError(
        String message,
        String displayMessage,
        Integer status,
        LocalDateTime timestamp
) {
}
