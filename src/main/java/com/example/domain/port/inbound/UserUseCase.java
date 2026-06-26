package com.example.domain.port.inbound;

import com.example.domain.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserUseCase {
  User register(String username, String password);

  Optional<User> findByUsername(String username);

  Optional<User> findById(UUID id);
}
