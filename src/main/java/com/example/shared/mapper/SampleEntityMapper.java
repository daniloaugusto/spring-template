package com.example.shared.mapper;

import com.example.domain.model.Sample;
import com.example.infrastructure.persistence.entity.SampleEntity;
import org.springframework.stereotype.Component;

@Component
public class SampleEntityMapper {

    public SampleEntity toEntity(Sample domain) {
        return new SampleEntity(domain.getId(), domain.getName(), domain.getCreatedAt());
    }

    public Sample toDomain(SampleEntity entity) {
        return new Sample(entity.getId(), entity.getName(), entity.getCreatedAt());
    }
}
