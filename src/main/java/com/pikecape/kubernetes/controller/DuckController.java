package com.pikecape.kubernetes.controller;

import com.pikecape.kubernetes.model.Duck;
import com.pikecape.kubernetes.service.DuckService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping(path = "/api/ducks")
@RequiredArgsConstructor
public class DuckController {
  private final DuckService duckService;

  @GetMapping(path = "/{id}")
  public Duck findById(
      @PathVariable(name = "id") UUID id
  ) {
    return duckService.findByUid(id);
  }

  @GetMapping
  public List<Duck> findAll() {
    return duckService.findAll();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Duck create(
      @RequestBody Duck duck
  ) {
    return duckService.create(duck);
  }

  @PutMapping(path = "/{id}")
  public Duck update(
      @PathVariable(name = "id") UUID id,
      @RequestBody Duck duck
  ) {
    return duckService.update(id, duck);
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @PathVariable(name = "id") UUID id
  ) {
    duckService.deleteByUid(id);
  }
}
