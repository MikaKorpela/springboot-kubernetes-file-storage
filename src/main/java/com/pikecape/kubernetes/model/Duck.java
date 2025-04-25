package com.pikecape.kubernetes.model;


import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Builder(toBuilder = true)
@Value
public class Duck {
  UUID id;
  String name;
}
