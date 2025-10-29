package com.googlecalendarclonebackend.googlecalendarclonebackend.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
public class Event {
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private String id; // client-provided id (Date.now() or UUID)

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "label", length = 32)
  private String label; // indigo, gray, green, blue, red, purple

  @Column(name = "day", nullable = false)
  private Long day; // epoch millis at start of day as used by frontend
}
