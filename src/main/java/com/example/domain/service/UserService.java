package com.example.domain.service;

import com.example.domain.exception.ConflictException;
import com.example.domain.model.User;
import com.example.domain.port.inbound.UserUseCase;
import com.example.domain.port.outbound.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService implements UserUseCase {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public User register(String username, String password) {
    if (userRepository.findByUsername(username).isPresent()) {
      throw new ConflictException("Username '" + username + "' already exists");
    }
    var encoded = passwordEncoder.encode(password);
    var user = new User(username, encoded, "ROLE_USER");
    return userRepository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<User> findById(UUID id) {
    return userRepository.findById(id);
  }
}
