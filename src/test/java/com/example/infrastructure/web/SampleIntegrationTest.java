package com.example.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.BaseIntegrationTest;
import com.example.application.dto.request.SampleRequest;
import com.example.application.dto.response.SampleResponse;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SampleIntegrationTest extends BaseIntegrationTest {

  private String token;

  @BeforeAll
  void setUp() {
    token = registerAndLogin("sampleuser", "password");
  }

  @Test
  void createSample() {
    var request = new SampleRequest("created sample");
    var response =
        rest.exchange(
            "/api/samples", HttpMethod.POST, authHeader(token, request), SampleResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().name()).isEqualTo("created sample");
    assertThat(response.getBody().id()).isNotNull();
    assertThat(response.getBody().createdAt()).isNotNull();
  }

  @Test
  void listSamples() {
    var req1 = new SampleRequest("list sample a");
    var req2 = new SampleRequest("list sample b");
    rest.exchange("/api/samples", HttpMethod.POST, authHeader(token, req1), SampleResponse.class);
    rest.exchange("/api/samples", HttpMethod.POST, authHeader(token, req2), SampleResponse.class);

    var response =
        rest.exchange(
            "/api/samples",
            HttpMethod.GET,
            authHeader(token),
            new ParameterizedTypeReference<List<SampleResponse>>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);
  }

  @Test
  void getSampleById() {
    var request = new SampleRequest("get-by-id sample");
    var created =
        rest.exchange(
                "/api/samples", HttpMethod.POST, authHeader(token, request), SampleResponse.class)
            .getBody();
    assertThat(created).isNotNull();

    var response =
        rest.exchange(
            "/api/samples/{id}",
            HttpMethod.GET,
            authHeader(token),
            SampleResponse.class,
            created.id());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().id()).isEqualTo(created.id());
    assertThat(response.getBody().name()).isEqualTo("get-by-id sample");
  }

  @Test
  void getSampleById_notFound() {
    var id = UUID.randomUUID();
    var response =
        rest.exchange("/api/samples/{id}", HttpMethod.GET, authHeader(token), Void.class, id);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
