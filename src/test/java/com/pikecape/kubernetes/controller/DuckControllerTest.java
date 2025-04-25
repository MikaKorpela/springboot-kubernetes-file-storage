package com.pikecape.kubernetes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pikecape.kubernetes.model.Duck;
import com.pikecape.kubernetes.service.DuckService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DuckController.class)
@AutoConfigureMockMvc(addFilters = false)
class DuckControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private DuckService duckService;

  private final UUID id = UUID.randomUUID();
  private final Duck donald = Duck.builder()
      .id(id)
      .name("Donald")
      .build();

  @Test
  void testFindByUid() throws Exception {
    when(duckService.findByUid(any()))
        .thenReturn(donald);

    mockMvc.perform(get("/api/ducks/" + id))
        .andExpect(status().isOk());
  }

  @Test
  void testFindAll() throws Exception {
    when(duckService.findAll())
        .thenReturn(List.of(donald));

    mockMvc.perform(get("/api/ducks"))
        .andExpect(status().isOk());
  }

  @Test
  void testCreate() throws Exception {
    when(duckService.create(any()))
        .thenReturn(donald);

    mockMvc.perform(post("/api/ducks")
            .contentType("application/json")
            .content("{\"id\":\"" + id + "\"}")
        )
        .andExpect(status().isCreated());
  }

  @Test
  void testUpdate() throws Exception {
    when(duckService.update(any(), any()))
        .thenReturn(donald);

    mockMvc.perform(put("/api/ducks/" + id)
            .contentType("application/json")
            .content("{\"id\":\""+ id +"\"}")
        )
        .andExpect(status().isOk());
  }

  @Test
  void testDelete() throws Exception {
    mockMvc.perform(delete("/api/ducks/" + id))
        .andExpect(status().isNoContent());
  }
}
