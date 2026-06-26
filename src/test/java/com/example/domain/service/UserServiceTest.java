package com.example.domain.service;

import com.example.domain.model.User;
import com.example.domain.port.outbound.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawpass")).thenReturn("encoded");
        var saved = new User(UUID.randomUUID(), "testuser", "encoded", "ROLE_USER", null);
        when(userRepository.save(any())).thenReturn(saved);

        var result = userService.register("testuser", "rawpass");

        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("encoded");
        assertThat(result.getRole()).isEqualTo("ROLE_USER");
        verify(passwordEncoder).encode("rawpass");
        verify(userRepository).save(any());
    }

    @Test
    void registerDuplicate() {
        var existing = new User(UUID.randomUUID(), "testuser", "encoded", "ROLE_USER", null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> userService.register("testuser", "rawpass"))
                .isInstanceOf(com.example.domain.exception.ConflictException.class)
                .hasMessageContaining("already exists");

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void findByUsername_found() {
        var user = new User(UUID.randomUUID(), "testuser", "pass", "ROLE_USER", null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        var result = userService.findByUsername("testuser");

        assertThat(result).isPresent().contains(user);
    }

    @Test
    void findByUsername_notFound() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        var result = userService.findByUsername("nobody");

        assertThat(result).isEmpty();
    }

    @Test
    void findById_found() {
        var id = UUID.randomUUID();
        var user = new User(id, "testuser", "pass", "ROLE_USER", null);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        var result = userService.findById(id);

        assertThat(result).isPresent().contains(user);
    }

    @Test
    void findById_notFound() {
        var id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        var result = userService.findById(id);

        assertThat(result).isEmpty();
    }
}
