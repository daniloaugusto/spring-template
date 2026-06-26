package com.example.shared.mapper;

import com.example.application.dto.response.SampleResponse;
import com.example.domain.model.Sample;
import org.springframework.stereotype.Component;

@Component
public class SampleMapper {

  public SampleResponse toResponse(Sample sample) {
    return new SampleResponse(sample.getId(), sample.getName(), sample.getCreatedAt());
  }
}
