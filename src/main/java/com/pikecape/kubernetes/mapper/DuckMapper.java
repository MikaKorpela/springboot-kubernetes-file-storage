package com.pikecape.kubernetes.mapper;

import com.pikecape.kubernetes.model.Duck;
import com.pikecape.kubernetes.repository.entity.DuckEntity;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DuckMapper {
  public Duck toDto(DuckEntity duckEntity) {
    return Optional.ofNullable(duckEntity)
        .map(source -> Duck.builder()
            .id(source.getId())
            .name(source.getName())
            .build()
        )
        .orElse(null);
  }

  public DuckEntity toEntity(Duck duck) {
    return Optional.ofNullable(duck)
        .map(source -> DuckEntity.builder()
            .id(source.getId())
            .name(source.getName())
            .build()
        )
        .orElse(null);
  }

}
