package com.example.infrastructure.web;

import com.example.application.dto.request.SampleRequest;
import com.example.application.dto.response.SampleResponse;
import com.example.domain.exception.NotFoundException;
import com.example.domain.port.inbound.SampleUseCase;
import com.example.shared.mapper.SampleMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/samples")
public class SampleController {

    private final SampleUseCase sampleUseCase;
    private final SampleMapper sampleMapper;

    public SampleController(SampleUseCase sampleUseCase, SampleMapper sampleMapper) {
        this.sampleUseCase = sampleUseCase;
        this.sampleMapper = sampleMapper;
    }

    @PostMapping
    public ResponseEntity<SampleResponse> create(@RequestBody @Valid SampleRequest request) {
        var sample = sampleUseCase.create(request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(sampleMapper.toResponse(sample));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SampleResponse> findById(@PathVariable UUID id) {
        var sample = sampleUseCase.findById(id)
                .orElseThrow(() -> new NotFoundException("Sample", id));
        return ResponseEntity.ok(sampleMapper.toResponse(sample));
    }

    @GetMapping
    public ResponseEntity<List<SampleResponse>> findAll() {
        var samples = sampleUseCase.findAll().stream()
                .map(sampleMapper::toResponse)
                .toList();
        return ResponseEntity.ok(samples);
    }
}
