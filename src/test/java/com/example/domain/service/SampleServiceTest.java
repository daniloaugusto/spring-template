package com.example.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.domain.model.Sample;
import com.example.domain.port.outbound.SampleRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SampleServiceTest {

  @Mock private SampleRepository sampleRepository;

  @InjectMocks private SampleService sampleService;

  @Test
  void create() {
    var saved = new Sample(UUID.randomUUID(), "test", null);
    when(sampleRepository.save(any())).thenReturn(saved);

    var result = sampleService.create("test");

    assertThat(result).isEqualTo(saved);
    verify(sampleRepository).save(any());
  }

  @Test
  void findById_found() {
    var id = UUID.randomUUID();
    var sample = new Sample(id, "test", null);
    when(sampleRepository.findById(id)).thenReturn(Optional.of(sample));

    var result = sampleService.findById(id);

    assertThat(result).isPresent().contains(sample);
  }

  @Test
  void findById_notFound() {
    var id = UUID.randomUUID();
    when(sampleRepository.findById(id)).thenReturn(Optional.empty());

    var result = sampleService.findById(id);

    assertThat(result).isEmpty();
  }

  @Test
  void findAll() {
    var samples =
        List.of(new Sample(UUID.randomUUID(), "a", null), new Sample(UUID.randomUUID(), "b", null));
    when(sampleRepository.findAll()).thenReturn(samples);

    var result = sampleService.findAll();

    assertThat(result).hasSize(2);
  }
}
