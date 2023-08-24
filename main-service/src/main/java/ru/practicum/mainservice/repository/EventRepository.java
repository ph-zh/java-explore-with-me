package ru.practicum.mainservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.mainservice.model.Event;

import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    boolean existsByCategoryId(int catId);

    Page<Event> findAllByInitiatorId(long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    Set<Event> findAllByIdIn(Set<Long> ids);
}
