package brittaju.reservation.availability;

public record AvailabilityResponse(
        String message,
        AvailabilityStatus status
) {}
