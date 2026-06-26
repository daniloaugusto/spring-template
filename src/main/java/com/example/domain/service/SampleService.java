package com.example.domain.service;

import com.example.domain.model.Sample;
import com.example.domain.port.inbound.SampleUseCase;
import com.example.domain.port.outbound.SampleRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SampleService implements SampleUseCase {

  private final SampleRepository sampleRepository;

  public SampleService(SampleRepository sampleRepository) {
    this.sampleRepository = sampleRepository;
  }

  @Override
  public Sample create(String name) {
    var sample = new Sample(name);
    return sampleRepository.save(sample);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Sample> findById(UUID id) {
    return sampleRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Sample> findAll() {
    return sampleRepository.findAll();
  }
}
