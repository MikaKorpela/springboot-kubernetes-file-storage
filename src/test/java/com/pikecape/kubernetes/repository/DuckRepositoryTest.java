package com.pikecape.kubernetes.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;

import com.pikecape.kubernetes.repository.entity.DuckEntity;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class DuckRepositoryTest {

  @Autowired
  private DuckRepository personJsonRepository;

  @TestConfiguration
  static class ContextConfiguration {
    @Bean
    public DuckRepository duckRepository() {
      return new DuckRepository("src/test/resources/ducks.json");
    }
  }

  @BeforeEach
  void setUp() { cleanup(); }

  private final DuckEntity donald = DuckEntity.builder()
      .name("Donald")
      .build();

  private final DuckEntity daisy = DuckEntity.builder()
      .name("Daisy")
      .build();

  @Test
  void testFindByUid() {
    DuckEntity created = personJsonRepository.create(donald);
    UUID id = created.getId();

    DuckEntity result = personJsonRepository.findByUid(id);

    assertEquals(donald.getName(), result.getName());
  }

  @Test
  void testFindAll() {
    personJsonRepository.create(donald);
    personJsonRepository.create(daisy);

    List<DuckEntity> result = personJsonRepository.findAll();

    assertEquals(2, result.size());
  }

  @Test
  void testCreateDuck() {
    personJsonRepository.create(donald);

    List<DuckEntity> result = personJsonRepository.findAll();

    assertEquals(1, result.size());
    assertNotNull(result.getFirst().getId());
    assertEquals(donald.getName(), result.getFirst().getName());
  }

  @Test
  void testUpdateDuck() {
    DuckEntity created = personJsonRepository.create(donald);

    List<DuckEntity> result = personJsonRepository.findAll();

    assertEquals(1, result.size());
    assertEquals(donald.getName(), result.getFirst().getName());

    created = created.toBuilder()
        .name("Duey")
        .build();
    personJsonRepository.update(created);

    result = personJsonRepository.findAll();

    assertEquals(1, result.size());
    assertEquals("Duey", result.getFirst().getName());
  }

  @Test
  void testDeleteDuck() {
    DuckEntity created = personJsonRepository.create(donald);
    UUID id = created.getId();

    List<DuckEntity> result = personJsonRepository.findAll();

    assertEquals(1, result.size());

    personJsonRepository.deleteByUid(id);

    result = personJsonRepository.findAll();

    assertEquals(0, result.size());
  }

  private void cleanup() {
    personJsonRepository.findAll().forEach(duck ->
        personJsonRepository.deleteByUid(duck.getId())
    );
  }
}
