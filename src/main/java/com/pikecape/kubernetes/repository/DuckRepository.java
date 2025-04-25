package com.pikecape.kubernetes.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pikecape.kubernetes.repository.entity.DuckEntity;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class DuckRepository {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private List<DuckEntity> duckEntities = new ArrayList<>();

  private final String jsonFilePath;

  public DuckRepository(@Value("${pikecape.duck.json-file-path}") String jsonFilePath) {
    this.jsonFilePath = jsonFilePath;

    loadPersonsFromJsonFile();
  }

  private void loadPersonsFromJsonFile() {
    try {
      File file = new File(this.jsonFilePath);
      if (!file.exists()) {
        throw new IOException("File not found: " + this.jsonFilePath);
      }

      duckEntities.addAll(objectMapper.readValue(file, new TypeReference<List<DuckEntity>>() {}));
    } catch (IOException exception) {
      throw new RuntimeException("Failed to load ducks from JSON file: " + exception.getMessage());
    }
  }

  public void savePersonsToJsonFile() {
    try {
      File file = new File(this.jsonFilePath);
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, duckEntities);
    } catch (IOException exception) {
      throw new RuntimeException("Failed to save ducks from JSON file: " + exception.getMessage());
    }
  }

  public Optional<DuckEntity> findById(UUID uid) {
    return duckEntities.stream()
        .filter(p -> Objects.equals(p.getId(), uid))
        .findFirst();
  }

  public List<DuckEntity> findAll() {
    return duckEntities;
  }

  public DuckEntity create(DuckEntity duckEntity) {
    try {
      if (duckEntity.getId() == null) {
        duckEntity = duckEntity.toBuilder()
            .id(UUID.randomUUID())
            .build();
      }

      duckEntities.add(duckEntity);

      savePersonsToJsonFile();

      return duckEntity;
    } catch (Exception exception) {
      throw new RuntimeException("Failed to create duck: " + exception.getMessage());
    }
  }

  public DuckEntity update(DuckEntity duckEntity) {
    try {
      duckEntities = duckEntities.stream()
          .map(p -> p.getId().equals(duckEntity.getId()) ? duckEntity : p)
          .collect(Collectors.toList());

      savePersonsToJsonFile();

      return duckEntity;
    } catch (Exception exception) {
      throw new RuntimeException("Failed to update duck: " + exception.getMessage());
    }
  }

  public void delete(DuckEntity duckEntity) {
    try {
      duckEntities = duckEntities.stream()
          .filter(p -> !Objects.equals(p.getId(), duckEntity.getId()))
          .collect(Collectors.toList());

      savePersonsToJsonFile();
    } catch (Exception exception) {
      throw new RuntimeException("Failed to delete duck: " + exception.getMessage());
    }
  }
}
