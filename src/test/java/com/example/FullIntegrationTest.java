package com.example;

import com.example.application.dto.request.LoginRequest;
import com.example.application.dto.request.SampleRequest;
import com.example.application.dto.response.LoginResponse;
import com.example.application.dto.response.SampleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FullIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private TestRestTemplate rest;

    @Test
    void fullFlow() {
        registerUser();
        var token = loginAndGetToken();
        var sampleId = createSample(token);
        listSamples(token, sampleId);
        getSampleById(token, sampleId);
    }

    private void registerUser() {
        var request = new LoginRequest("testuser", "testpass");
        var response = rest.postForEntity("/api/auth/register", request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private String loginAndGetToken() {
        var request = new LoginRequest("testuser", "testpass");
        var response = rest.postForEntity("/api/auth/login", request, LoginResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isNotBlank();
        return response.getBody().token();
    }

    private UUID createSample(String token) {
        var request = new SampleRequest("integration test sample");
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        var entity = new HttpEntity<>(request, headers);
        var response = rest.exchange("/api/samples", HttpMethod.POST, entity, SampleResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("integration test sample");
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().createdAt()).isNotNull();
        return response.getBody().id();
    }

    private void listSamples(String token, UUID expectedId) {
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        var entity = new HttpEntity<>(headers);
        var response = rest.exchange("/api/samples", HttpMethod.GET, entity,
                new ParameterizedTypeReference<List<SampleResponse>>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).anyMatch(s -> s.id().equals(expectedId));
    }

    private void getSampleById(String token, UUID id) {
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        var entity = new HttpEntity<>(headers);
        var response = rest.exchange("/api/samples/{id}", HttpMethod.GET, entity,
                SampleResponse.class, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(id);
        assertThat(response.getBody().name()).isEqualTo("integration test sample");
    }
}
