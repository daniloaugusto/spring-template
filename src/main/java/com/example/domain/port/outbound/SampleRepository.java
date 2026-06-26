package com.example.domain.port.outbound;

import com.example.domain.model.Sample;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SampleRepository {
  Sample save(Sample sample);

  Optional<Sample> findById(UUID id);

  List<Sample> findAll();
}
