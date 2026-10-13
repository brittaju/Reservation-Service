package brittaju.reservation.reservations;

public record ReservationSearchFilter(
        Long userId,
        Long roomId,
        Integer pageSize,
        Integer pageNumber
) {}
