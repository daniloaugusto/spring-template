package com.example.domain.port.inbound;

import com.example.domain.model.Sample;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SampleUseCase {
  Sample create(String name);

  Optional<Sample> findById(UUID id);

  List<Sample> findAll();
}
