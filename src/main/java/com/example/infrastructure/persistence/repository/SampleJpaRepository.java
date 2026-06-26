package com.example.infrastructure.persistence.repository;

import com.example.infrastructure.persistence.entity.SampleEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleJpaRepository extends JpaRepository<SampleEntity, UUID> {}
