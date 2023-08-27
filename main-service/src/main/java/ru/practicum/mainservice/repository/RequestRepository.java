package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainservice.model.Request;
import ru.practicum.mainservice.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByEventId(long eventId);

    List<Request> findAllByRequesterId(long userId);

    List<Request> findAllByIdIn(List<Long> ids);

    List<Request> findAllByEventIdInAndStatusEquals(List<Long> ids, RequestStatus status);

    int countAllByEventIdAndStatusEquals(long eventId, RequestStatus status);

    @Query("select r from Request r where r.event.id in ?1 and r.status = ?2")
    List<Request> findAllConfirmedRequestsByEventIdsAndStatus(List<Long> ids, RequestStatus status);
}
