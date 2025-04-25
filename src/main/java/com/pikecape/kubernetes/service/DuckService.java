package com.pikecape.kubernetes.service;

import com.pikecape.kubernetes.mapper.DuckMapper;
import com.pikecape.kubernetes.model.Duck;
import com.pikecape.kubernetes.repository.DuckRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class DuckService {
  private final DuckRepository duckRepository;

  private static final String DUCK_NOT_FOUND = "Duck not found";

  public Duck findByUid(UUID id) {
    return Optional.ofNullable(duckRepository.findByUid(id))
        .map(DuckMapper::toDto)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, DUCK_NOT_FOUND));
  }

  public List<Duck> findAll() {
    return duckRepository.findAll().stream()
        .map(DuckMapper::toDto)
        .toList();
  }

  public Duck create(Duck duck) {
    return DuckMapper.toDto(duckRepository.create(DuckMapper.toEntity(duck)));
  }

  public Duck update(UUID id, Duck duck) {
    Optional.ofNullable(duckRepository.findByUid(id))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, DUCK_NOT_FOUND));

    duck = duck.toBuilder()
        .id(id)
        .build();

    return DuckMapper.toDto(duckRepository.update(DuckMapper.toEntity(duck)));
  }

  public void deleteByUid(UUID id) {
    Optional.ofNullable(duckRepository.findByUid(id))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, DUCK_NOT_FOUND));

    duckRepository.deleteByUid(id);
  }
}
