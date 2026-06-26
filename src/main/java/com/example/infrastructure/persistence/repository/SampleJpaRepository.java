package com.example.infrastructure.persistence.repository;

import com.example.infrastructure.persistence.entity.SampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SampleJpaRepository extends JpaRepository<SampleEntity, UUID> {
}
