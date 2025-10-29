package com.googlecalendarclonebackend.googlecalendarclonebackend.event;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventController {
  private final EventService service;

  public EventController(EventService service) {
    this.service = service;
  }

  @GetMapping
  public List<Event> list(@RequestParam(required = false) Long start, @RequestParam(required = false) Long end) {
    return service.listAll(start, end);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Event> get(@PathVariable String id) {
    return service.find(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public Event create(@RequestBody Event e) {
    return service.create(e);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Event> update(@PathVariable String id, @RequestBody Event e) {
    if (service.find(id).isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(service.update(id, e));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    if (service.find(id).isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
