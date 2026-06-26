package com.example.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.BaseIntegrationTest;
import com.example.application.dto.request.LoginRequest;
import com.example.application.dto.request.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class AuthIntegrationTest extends BaseIntegrationTest {

  @Test
  void registerAndLogin() {
    var token = registerAndLogin("authuser", "password");
    assertThat(token).isNotBlank();
  }

  @Test
  void registerDuplicateReturnsConflict() {
    registerAndLogin("dupeuser", "password");
    var request = new RegisterRequest("dupeuser", "password");
    var response = rest.postForEntity("/api/auth/register", request, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void loginWrongPasswordReturnsUnauthorized() {
    registerAndLogin("wrongpassuser", "correctpass");
    var request = new LoginRequest("wrongpassuser", "wrongpass");
    var response = rest.postForEntity("/api/auth/login", request, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
