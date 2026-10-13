package brittaju.reservation.reservations;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations(
            @RequestParam("userId") Long userId,
            @RequestParam("roomId") Long roomId,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam("pageNumber") Integer pageNumber
    ) {
        ReservationSearchFilter filter = new ReservationSearchFilter(
                userId,
                roomId,
                pageSize,
                pageNumber
        );
        return ResponseEntity.ok(reservationService.searchAllByFilter(filter));
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation (
            @RequestBody @Valid Reservation reservationToCreate
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(reservationToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation (
            @PathVariable("id") Long id,
            @RequestBody @Valid Reservation reservationToUpdate
    ) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservationToUpdate));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation (
            @PathVariable("id") Long id
    ) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok()
                .build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation (
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(reservationService.approveReservation(id));
    }
}
