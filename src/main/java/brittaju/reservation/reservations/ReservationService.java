package brittaju.reservation.reservations;

import brittaju.reservation.availability.AvailabilityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository repository;
    private final ReservationMapper mapper;
    private final AvailabilityService availabilityService;
    private final RedisTemplate<String, ReservationEntity> redisTemplate;

    @Cacheable(
            value = "reservation",
            key = "#id"
    )
    public Reservation getReservationById(Long id) {
//        ReservationEntity objectFromCache = redisTemplate
//                .opsForValue()
//                .get("reservation:" + id);
//        if (objectFromCache != null) {
//            return mapper.toDomain(objectFromCache);
//        }
        ReservationEntity entityFromDb = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found reservation by id " + id));
//        redisTemplate
//                .opsForValue()
//                .set("reservation:" + id,
//                        entityFromDb,
//                        Duration.ofMinutes(1));
        return mapper.toDomain(entityFromDb);
    }

    public List<Reservation> searchAllByFilter(ReservationSearchFilter filter) {
        int pageSize = filter.pageSize() != null
                ? filter.pageSize()
                : 10;
        int pageNumber = filter.pageNumber() != null
                ? filter.pageNumber()
                : 0;
        Pageable pageable = Pageable
                .ofSize(pageSize)
                .withPage(pageNumber);
        List<ReservationEntity> allEntities = repository.searchAllByFilter(
                filter.userId(),
                filter.roomId(),
                pageable
        );
        return allEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    public Reservation createReservation(Reservation reservationToCreate) {
        if (reservationToCreate.status() != null) {
            throw new IllegalArgumentException(
                    "Status should be empty");
        }
        if (!reservationToCreate.endDate().isAfter(reservationToCreate.startDate())) {
            throw new IllegalArgumentException(
                    "Start date must be 1 day earlier than end date");
        }
        ReservationEntity newEntity = mapper.toEntity(reservationToCreate);
        newEntity.setStatus(ReservationStatus.PENDING);
        ReservationEntity savedEntity = repository.save(newEntity);
        return mapper.toDomain(savedEntity);
    }

    @CacheEvict(
            value = "reservation",
            key = "#id"
    )
    public Reservation updateReservation(
            Long id,
            Reservation reservationToUpdate
    ) {
        ReservationEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found reservation by id " + id));
        if (entity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot modify reservation: status = " + entity.getStatus());
        }
        if (!reservationToUpdate.endDate().isAfter(reservationToUpdate.startDate())) {
            throw new IllegalArgumentException(
                    "Start date must be 1 day earlier than end date");
        }
        ReservationEntity entityToSave = mapper.toEntity(reservationToUpdate);
        entityToSave.setId(entity.getId());
        entityToSave.setStatus(ReservationStatus.PENDING);
        repository.save(entityToSave);
//        ReservationEntity objectFromCache = redisTemplate
//                .opsForValue()
//                .get("reservation:" + id);
//        if (objectFromCache != null) {
//            redisTemplate.delete("reservation:" + id);
//        }
        return mapper.toDomain(entityToSave);
    }

    @CacheEvict(
            value = "reservation",
            key = "#id"
    )
    public void cancelReservation(Long id) {
        ReservationEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No found reservation by id " + id));
        if (entity.getStatus().equals(ReservationStatus.APPROVED)) {
            throw new IllegalStateException(
                    "Cannot cancel approved reservation");
        }
        if (entity.getStatus().equals(ReservationStatus.CANCELLED)) {
            throw new IllegalStateException(
                    "Cannot cancel cancelled reservation");
        }
        repository.setStatus(id, ReservationStatus.CANCELLED);
//        ReservationEntity objectFromCache = redisTemplate
//                .opsForValue()
//                .get("reservation:" + id);
//        if (objectFromCache != null) {
//            redisTemplate.delete("reservation:" + id);
//        }
    }

    @CacheEvict(
            value = "reservation",
            key = "#id"
    )
    public Reservation approveReservation(Long id) {
        ReservationEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No found reservation by id " + id));
        if (entity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot modify reservation: status = " + entity.getStatus());
        }
        if (!availabilityService.isReservationAvailable(
                entity.getRoomId(),
                entity.getStartDate(),
                entity.getEndDate()
        )) {
            throw new IllegalStateException(
                    "Cannot modify reservation, conflict");
        }
        entity.setStatus(ReservationStatus.APPROVED);
        repository.save(entity);
//        ReservationEntity objectFromCache = redisTemplate
//                .opsForValue()
//                .get("reservation:" + id);
//        if (objectFromCache != null) {
//            redisTemplate.delete("reservation:" + id);
//        }
        return mapper.toDomain(entity);
    }
}
