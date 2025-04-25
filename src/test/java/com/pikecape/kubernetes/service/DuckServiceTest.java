package com.pikecape.kubernetes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.pikecape.kubernetes.mapper.DuckMapper;
import com.pikecape.kubernetes.model.Duck;
import com.pikecape.kubernetes.repository.DuckRepository;
import com.pikecape.kubernetes.repository.entity.DuckEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class DuckServiceTest {
  @Autowired
  private DuckService duckService;

  @MockitoBean
  private DuckRepository duckRepository;

  @TestConfiguration
  static class ContextConfiguration {
    @Bean
    public DuckService duckService(DuckRepository duckRepository) {
      return new DuckService(duckRepository);
    }
  }

  private final UUID id1 = UUID.randomUUID();
  private final DuckEntity donald = DuckEntity.builder()
      .id(id1)
      .name("Donald")
      .build();

  private final UUID id2 = UUID.randomUUID();
  private final DuckEntity daisy = DuckEntity.builder()
      .id(id2)
      .name("Daisy")
      .build();

  @Test
  void testFindByUid() {
    when(duckRepository.findById(id1))
        .thenReturn(Optional.of(donald));

    Duck result = duckService.findByUid(id1);

    assertEquals(donald.getName(), result.getName());
  }

  @Test
  void testFindAll() {
    when(duckRepository.findAll()).thenReturn(List.of(donald, daisy));

    List<Duck> result = duckService.findAll();

    assertEquals(2, result.size());
  }

  @Test
  void testCreateDuck() {
    Duck duck = DuckMapper.toDto(donald);
    duck = duck.toBuilder()
        .id(null)
        .build();

    when(duckRepository.create(any()))
        .then(invocation -> {
          DuckEntity createdDuck = (DuckEntity) invocation.getArguments()[0];
          return createdDuck.toBuilder().id(id1).build();
        });

    Duck result = duckService.create(duck);

    assertEquals(id1, result.getId());
    assertEquals(duck.getName(), result.getName());
  }

  @Test
  void testUpdateDuck() {
    Duck duck = DuckMapper.toDto(donald);

    when(duckRepository.findById(id1))
        .thenReturn(Optional.of(donald));

    when(duckRepository.update(any()))
        .then(invocation -> invocation.getArguments()[0]);

    Duck result = duckService.update(duck.getId(), duck);

    assertEquals(id1, result.getId());
    assertEquals(duck.getName(), result.getName());
  }

  @Test
  void testDeletePerson() {
    when(duckRepository.findById(id1))
        .thenReturn(Optional.of(donald));

    duckService.deleteByUid(id1);
  }
}
