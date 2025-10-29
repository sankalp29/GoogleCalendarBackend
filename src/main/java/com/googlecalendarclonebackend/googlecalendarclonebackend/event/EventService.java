package com.googlecalendarclonebackend.googlecalendarclonebackend.event;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EventService {
  private final EventRepository repository;

  public EventService(EventRepository repository) {
    this.repository = repository;
  }

  public List<Event> listAll(Long start, Long end) {
    if (start != null && end != null) {
      return repository.findByDayBetweenOrderByDayAsc(start, end);
    }
    return repository.findAll().stream()
        .sorted((a, b) -> Long.compare(a.getDay(), b.getDay()))
        .toList();
  }

  public Optional<Event> find(String id) {
    return repository.findById(id);
  }

  public Event create(Event e) {
    validate(e);
    if (!StringUtils.hasText(e.getId())) {
      throw new IllegalArgumentException("id is required (client-generated)");
    }
    return repository.save(e);
  }

  public Event update(String id, Event e) {
    validate(e);
    e.setId(id);
    return repository.save(e);
  }

  public void delete(String id) {
    repository.deleteById(id);
  }

  private void validate(Event e) {
    if (!StringUtils.hasText(e.getTitle())) {
      throw new IllegalArgumentException("title is required");
    }
    if (e.getDay() == null) {
      throw new IllegalArgumentException("day (epoch millis) is required");
    }
  }
}
