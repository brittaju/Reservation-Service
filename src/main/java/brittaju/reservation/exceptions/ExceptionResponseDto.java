package brittaju.reservation.exceptions;

import java.time.LocalDateTime;

public record ExceptionResponseDto(
        String message,
        String detailedMessage,
        LocalDateTime errorTime
) {}
