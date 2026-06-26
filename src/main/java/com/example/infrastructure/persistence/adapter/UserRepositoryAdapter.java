package com.example.infrastructure.persistence.adapter;

import com.example.domain.model.User;
import com.example.domain.port.outbound.UserRepository;
import com.example.infrastructure.persistence.repository.UserJpaRepository;
import com.example.shared.mapper.UserEntityMapper;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryAdapter implements UserRepository {

  private final UserJpaRepository jpaRepository;
  private final UserEntityMapper entityMapper;

  public UserRepositoryAdapter(UserJpaRepository jpaRepository, UserEntityMapper entityMapper) {
    this.jpaRepository = jpaRepository;
    this.entityMapper = entityMapper;
  }

  @Override
  public User save(User user) {
    var entity = entityMapper.toEntity(user);
    var saved = jpaRepository.save(entity);
    return entityMapper.toDomain(saved);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return jpaRepository.findByUsername(username).map(entityMapper::toDomain);
  }

  @Override
  public Optional<User> findById(UUID id) {
    return jpaRepository.findById(id).map(entityMapper::toDomain);
  }
}
