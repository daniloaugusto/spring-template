package com.example;

import com.example.application.dto.request.LoginRequest;
import com.example.application.dto.request.RegisterRequest;
import com.example.application.dto.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfig.class)
public abstract class BaseIntegrationTest {

    @Autowired
    protected TestRestTemplate rest;

    protected String registerAndLogin(String username, String password) {
        var registerRequest = new RegisterRequest(username, password);
        var registerResponse = rest.postForEntity("/api/auth/register", registerRequest, Void.class);
        assertThat(registerResponse.getStatusCode().is2xxSuccessful()).isTrue();

        var loginRequest = new LoginRequest(username, password);
        var loginResponse = rest.postForEntity("/api/auth/login", loginRequest, LoginResponse.class);
        assertThat(loginResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().token()).isNotBlank();
        return loginResponse.getBody().token();
    }

    protected HttpEntity<Void> authHeader(String token) {
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }

    protected <T> HttpEntity<T> authHeader(String token, T body) {
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(body, headers);
    }
}
