package com.example.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

  private UUID id;
  private String username;
  private String password;
  private String role;
  private LocalDateTime createdAt;

  public User() {}

  public User(String username, String password, String role) {
    this.id = UUID.randomUUID();
    this.username = username;
    this.password = password;
    this.role = role;
    this.createdAt = LocalDateTime.now();
  }

  public User(UUID id, String username, String password, String role, LocalDateTime createdAt) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.role = role;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
