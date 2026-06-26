package com.example.infrastructure.persistence.adapter;

import com.example.domain.model.Sample;
import com.example.domain.port.outbound.SampleRepository;
import com.example.infrastructure.persistence.repository.SampleJpaRepository;
import com.example.shared.mapper.SampleEntityMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class SampleRepositoryAdapter implements SampleRepository {

  private final SampleJpaRepository jpaRepository;
  private final SampleEntityMapper entityMapper;

  public SampleRepositoryAdapter(
      SampleJpaRepository jpaRepository, SampleEntityMapper entityMapper) {
    this.jpaRepository = jpaRepository;
    this.entityMapper = entityMapper;
  }

  @Override
  public Sample save(Sample sample) {
    var entity = entityMapper.toEntity(sample);
    var saved = jpaRepository.save(entity);
    return entityMapper.toDomain(saved);
  }

  @Override
  public Optional<Sample> findById(UUID id) {
    return jpaRepository.findById(id).map(entityMapper::toDomain);
  }

  @Override
  public List<Sample> findAll() {
    return jpaRepository.findAll().stream().map(entityMapper::toDomain).toList();
  }
}
