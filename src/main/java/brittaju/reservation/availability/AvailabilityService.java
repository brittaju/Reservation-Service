package brittaju.reservation.availability;

import brittaju.reservation.reservations.ReservationRepository;
import brittaju.reservation.reservations.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final ReservationRepository repository;

    public boolean isReservationAvailable(
            Long roomId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException(
                    "Start date must be 1 day earlier than end date");
        }
        List<Long> conflictIds = repository.findConflictReservationsIds(
                roomId,
                startDate,
                endDate,
                ReservationStatus.APPROVED
        );
        return conflictIds.isEmpty();
    }
}
