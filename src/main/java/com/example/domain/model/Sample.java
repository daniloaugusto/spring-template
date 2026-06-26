package com.example.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Sample {

  private UUID id;
  private String name;
  private LocalDateTime createdAt;

  public Sample() {}

  public Sample(String name) {
    this.id = UUID.randomUUID();
    this.name = name;
    this.createdAt = LocalDateTime.now();
  }

  public Sample(UUID id, String name, LocalDateTime createdAt) {
    this.id = id;
    this.name = name;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
