package com.example.infrastructure.web;

import com.example.application.dto.request.SampleRequest;
import com.example.application.dto.response.SampleResponse;
import com.example.domain.exception.NotFoundException;
import com.example.domain.port.inbound.SampleUseCase;
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

    public SampleController(SampleUseCase sampleUseCase) {
        this.sampleUseCase = sampleUseCase;
    }

    @PostMapping
    public ResponseEntity<SampleResponse> create(@RequestBody @Valid SampleRequest request) {
        var sample = sampleUseCase.create(request.name());
        var response = new SampleResponse(sample.getId(), sample.getName(), sample.getCreatedAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SampleResponse> findById(@PathVariable UUID id) {
        var sample = sampleUseCase.findById(id)
                .orElseThrow(() -> new NotFoundException("Sample", id));
        return ResponseEntity.ok(new SampleResponse(sample.getId(), sample.getName(), sample.getCreatedAt()));
    }

    @GetMapping
    public ResponseEntity<List<SampleResponse>> findAll() {
        var samples = sampleUseCase.findAll().stream()
                .map(s -> new SampleResponse(s.getId(), s.getName(), s.getCreatedAt()))
                .toList();
        return ResponseEntity.ok(samples);
    }
}
