package brittaju.reservation.availability;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AvailabilityRequest(

        @NotNull
        Long roomId,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate

) {}
