package com.googlecalendarclonebackend.googlecalendarclonebackend.event;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {
  List<Event> findByDayBetweenOrderByDayAsc(Long startInclusive, Long endInclusive);
}
