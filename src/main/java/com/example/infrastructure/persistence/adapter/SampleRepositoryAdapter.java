package com.example.infrastructure.persistence.adapter;

import com.example.domain.model.Sample;
import com.example.domain.port.outbound.SampleRepository;
import com.example.infrastructure.persistence.entity.SampleEntity;
import com.example.infrastructure.persistence.repository.SampleJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SampleRepositoryAdapter implements SampleRepository {

    private final SampleJpaRepository jpaRepository;

    public SampleRepositoryAdapter(SampleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Sample save(Sample sample) {
        var entity = toEntity(sample);
        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Sample> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Sample> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private SampleEntity toEntity(Sample domain) {
        return new SampleEntity(domain.getId(), domain.getName(), domain.getCreatedAt());
    }

    private Sample toDomain(SampleEntity entity) {
        return new Sample(entity.getId(), entity.getName(), entity.getCreatedAt());
    }
}
