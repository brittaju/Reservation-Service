package brittaju.reservation.availability;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservation/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService service;

    @PostMapping("/check")
    public ResponseEntity<AvailabilityResponse> checkAvailability(
            @Valid AvailabilityRequest request
    ) {
        boolean isAvailable = service.isReservationAvailable(
                request.roomId(),
                request.startDate(),
                request.endDate()
        );
        String message = isAvailable
                ? "Room available to reservation"
                : "Room not available to reservation";
        AvailabilityStatus status = isAvailable
                ? AvailabilityStatus.AVAILABLE
                : AvailabilityStatus.RESERVED;
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AvailabilityResponse(
                        message,
                        status
                ));
    }
}
